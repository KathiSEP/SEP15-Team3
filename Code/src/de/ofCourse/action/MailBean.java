/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.UnsupportedEncodingException;
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


import de.ofCourse.model.SmtpServer;
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
    public void sendMail(String recipients, String sender,
	    String subject, String message) {
        //für testzwecke nur eine mail List<String>
        Properties prop = new Properties();
        
        
        Authenticator loginAuth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                PasswordAuthentication loginData = new PasswordAuthentication(smtpServer.getUsername(), smtpServer.getPassword());
                return loginData;
            }
            
            
        };
        
        
        // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
        prop.put("mail.smtp.host", this.smtpServer.getHostaddr());
        prop.put("mail.smtph.port", this.smtpServer.getPort());
        
        
        Session session = Session.getDefaultInstance(prop, loginAuth);
        
        try{
            // https://javamail.java.net/nonav/docs/api/
            MimeMessage mail = new MimeMessage(session);
            
            mail.setFrom(new InternetAddress(sender, "OfCourse"));
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress("sebastian@nrschwarz.de"));
            
            mail.setSubject("Testmail");
            mail.setText("Hallo Sebastian, Email versand funktioniert");
            
            Transport trans = session.getTransport("smtps");
            trans.connect(prop.getProperty("mail.smtp.host"), smtpServer.getUsername() , smtpServer.getPassword());
            trans.sendMessage(mail, mail.getAllRecipients());
            System.out.println("Mail wird verschickt");
            trans.close();
        }catch (MessagingException e){
            //TODO Logging
            System.out.println("Fehler!");
            
        } catch (UnsupportedEncodingException e){
            System.out.println("EingabeFehler");
            //TODO Logging
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
     */
    public void sendAuthentificationMessage(int userID) {
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

}