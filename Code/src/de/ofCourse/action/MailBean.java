/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;



import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;


import javax.faces.context.FacesContext;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.model.SmtpServer;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PropertyManager;

/**
 * Sends emails to lists of recipients and provides the service of sending
 * system-generated mails like the email - verification - mail.
 * <p>
 * The class has access to the SMTP - Server -settings which are needed to send
 * mails.
 * 
 * @author Tobias Fuchs
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class MailBean {

    /**
     * SMTP-server-object, that is needed to get the settings of the server.
     */
    private SmtpServer smtpServer;

    
    
    @PostConstruct
    private void init(){
        smtpSetup();        
    }


	private void smtpSetup() {
		smtpServer = new SmtpServer();
        smtpServer.setHostaddr(PropertyManager.getInstance().getPropertyMail("smtphost"));
        smtpServer.setPort(Integer.parseInt(PropertyManager.getInstance().getPropertyMail("smtpport")));
        //TODO fehlt noch
        smtpServer.setSsl(true);
        smtpServer.setUsername(PropertyManager.getInstance().getPropertyMail("mailusername"));
        smtpServer.setPassword(PropertyManager.getInstance().getPropertyMail("mailpassword"));
	}
    
    
    /**
     * 
     * Sends an email to a list of recipients. The email contains a subject and
     * a message.
     * 
     * @param recipients
     *            List of recipients who receive the message
     * @param sender
     *            sender of the email
     * @param subject
     *            subject of the email
     * @param message
     *            message of the email
     */
    public void sendMail(List<String> recipients, String subject, String message) {
        //für testzwecke nur eine mail 
        Properties prop = new Properties();
        smtpSetup();
        
        Authenticator loginAuth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                PasswordAuthentication loginData = new PasswordAuthentication(smtpServer.getUsername(), smtpServer.getPassword());
                return loginData;
            }          
        };
     
      
        // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
        prop.put("mail.smtp.host", smtpServer.getHostaddr());
        prop.put("mail.smtph.port", smtpServer.getPort());
        prop.put("mail.smtp.starttls.enable", "true");
        //prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.auth", "true");
        
        
        Session session = Session.getDefaultInstance(prop, loginAuth);
        
        try{
            // https://javamail.java.net/nonav/docs/api/
            MimeMessage mail = new MimeMessage(session);
            
            mail.setFrom(new InternetAddress(smtpServer.getUsername()));
            
            for(String mailAddresse : recipients){
                mail.addRecipients(Message.RecipientType.TO, mailAddresse);
            }
            
            
            mail.setSubject(subject);
            mail.setText(message);
                                   
            Transport transport = session.getTransport("smtp");
            
            // Eingabe von benutzerdaten und password
            transport.connect("smtp.gmail.com", smtpServer.getUsername(), smtpServer.getPassword());
            transport.sendMessage(mail, mail.getAllRecipients());
            transport.close();
            System.out.println("Mail wird verschickt");
        }catch (MessagingException e){
            //TODO Logging
            System.out.println("Fehler!");
            
        }
    }

    /**
     * Generates an authentication messages.<br>
     * This message is sent when a user registers himself in the system. The
     * message contains an verification link, the verifies the entered email and
     * activates the account in case of the type of account activation is set to
     * email verification.
     * 
     * 
     * @param userID
     *            ID of the user, who receives the message.
     * @param veriString 
     */
    public void sendAuthentificationMessage(int userID, String veriString) {
        
        // User who should get the authentificationMessage will be loaded from Database
        Transaction trans = new Connection();
        trans.start();
        User userToInform = UserDAO.getUser(trans, userID);
        
        // E-Mail Messenge:
        String messenge = "Welcome " + userToInform.getSalutation() + " " + userToInform.getLastname();
        messenge += " to the OfCourse Family. Thank you very much for your registration. \n";
        messenge += "Please press the following link to confirm your Mailaddress and to finish your authentication: \n" +
        messenge + createLink() + "/facelets/open/authenticate.xhtml?veri=" + veriString + "\n\n";
        
        String subject = "Authentication Mail";
        
        sendSingleMail(userToInform.getEmail(), subject, messenge);
        
    }

    /**
     * Generates a mail that requests an user to conform his email - address.<br>
     * This method is used if the user changes his email in his profile.
     * 
     * @param userID
     *            ID of the user, who receives the message.
     */
    public void sendConfirmationMessage(int userID) {
    }

    /**
     * Generates a message which is sent in case of a user has forgot his
     * password.<br>
     * The message contains the new generated password.
     * 
     * @param userID
     *            ID of the user, who receives the message.
     */
    public void sendNewPasswordMessage(int userID) {
    }

    /**
     * Returns the smtpServer - object that contains the settings of the smtp
     * server.
     * 
     * @return the smtpSever - object
     */
    public SmtpServer getSmtpServer() {
	return smtpServer;
    }

    /**
     * Sets smtpServer - object that contains the settings of the smtp server.
     * 
     * @param smtpServer
     *            smtpSserver - object to set
     */
    public void setSmtpServer(SmtpServer smtpServer) {
    }
    
    /**
     * @return
     */
    private String createLink(){
        
        String requestScheme = FacesContext.getCurrentInstance().getExternalContext().getRequestScheme();
        String requestHost = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
        String requestContextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        
        // arbeiten wir nur auf den standart ports?? dann evtl weglassen
        int requestPorts = FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
        
        String link = requestScheme + "://" + requestHost +":" + requestPorts + requestContextPath;
        return link;
    }
    
    
    private void sendSingleMail(String maildaddress, String subject, String messenge){
        List<String> recipients = new ArrayList<String>();
        recipients.add(maildaddress);
        sendMail(recipients, subject, messenge);
            
    }

}