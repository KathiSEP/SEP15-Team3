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

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.CourseUnitDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.SmtpServer;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.MailThread;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PropertyManager;

/**
 * Sends emails to lists of recipients and provides the service of sending
 * system-generated mails like the email - verification - mail.
 * <p>
 * The class has access to the SMTP - Server -settings which are needed to send
 * mails.
 * 
 * @author Sebastian Schwarz
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

        //Tests whether the admin wants to connect per SSL or TTLS
        if(PropertyManager.getInstance().getPropertyMail("useSSL") == "true"){
            smtpServer.setSsl(true);
        } else {
            smtpServer.setSsl(false);
        }
        
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
        //f�r testzwecke nur eine mail 
        Properties prop = new Properties();
        smtpSetup();
        
        Authenticator loginAuth = new Authenticator() {
           
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                PasswordAuthentication loginData = 
                        new PasswordAuthentication
                        (smtpServer.getUsername(), smtpServer.getPassword());
                
                return loginData;
            }          
        };
     
      
        // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
        prop.put("mail.smtp.ssl.trust", smtpServer.getHostaddr());
        prop.put("mail.smtph.port", smtpServer.getPort());
        prop.put("mail.smtp.auth", "true");
        
        if(smtpServer.isSsl()){
            prop.put("mail.smtp.ssl.enable", "true");
        }else{
            prop.put("mail.smtp.starttls.enable", "true"); 
        }                        

        
        
        Thread t = new Thread(new MailThread(prop, loginAuth, smtpServer, recipients, subject, message){

            @Override
            public void run() {
                try{
                    // https://javamail.java.net/nonav/docs/api/
                    Session session = Session.getDefaultInstance(prop, loginAuth);
                    MimeMessage mail = new MimeMessage(session);
                    
                    mail.setFrom(new InternetAddress(smtpServer.getUsername()));
                    
                    for(String mailAddresse : recipients){
                        mail.setRecipients(Message.RecipientType.BCC, mailAddresse);
                    }
                    
                    
                    mail.setSubject(subject);
                    mail.setText(message);
                                           
                    Transport transport = session.getTransport("smtp");
                    
                    
                    transport.connect(smtpServer.getHostaddr(), smtpServer.getUsername(), smtpServer.getPassword());
                    transport.sendMessage(mail, mail.getAllRecipients());
                    transport.close();
                    
                    
                }catch (MessagingException e){
                    LogHandler.getInstance().error("Error occured while sending mail");
                    
                }
            }
            
        });
        t.start();
        
        
        
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
        Transaction trans = Connection.create();
        trans.start();
        try{
           User userToInform = UserDAO.getUser(trans, userID); 
           trans.commit();
         //TODO try catch?
           // E-Mail Messenge:
           String messenge = createSalutation(userToInform);
           messenge += "Welcome to the OfCourse Family. Thank you very much for your registration. \n";
           messenge += "Please press the following link to confirm your Mailaddress and to finish your authentication: \n \n";
           
                   //koennte man noch schoener machen
                   
           messenge += createLink() + "/facelets/open/authenticate.xhtml?veri=" + veriString + "\n\n";
           messenge += createSignature();
           
           String subject = "Authentication Mail";
           
           sendSingleMail(userToInform.getEmail(), subject, messenge);
        } catch (InvalidDBTransferException e){
           trans.rollback(); 
        }
        
        
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
        this.smtpServer = smtpServer;
    }
    
    /**
     * 
     * 
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


    /**
     * 
     * 
     * @param newPassword
     * @param email
     */
    public void sendMailForLostPassword(String newPassword, String email) {
        Transaction trans = Connection.create();
        trans.start();
        
        try{
           User userToInform = UserDAO.getUserPerMail(trans, email);
           
           String subject = "OfCourse new Password Email";
           
           String message = createLostPasswordMail(newPassword, userToInform);
           
           sendSingleMail(userToInform.getEmail(), subject, message);
           LogHandler.getInstance().debug("Lost Password Mail sended");
           
           trans.commit();
        } catch (InvalidDBTransferException e){
            
            LogHandler.getInstance().error("Error occured during getUser in the sendLostPassword Methode");
            trans.rollback();
        }
        
    }


    /**
     * @param newPassword
     * @param userToInform
     * @return 
     */
    private String createLostPasswordMail(String newPassword, User userToInform) {
        String message = createSalutation(userToInform) ;
           message += "Your new Password is: " + newPassword + "\n";
           message += "Please change your Password after the next login \n\n";
           message += createLink() + " \n";
           message += createSignature();
           
           return message;
    }
    
    
    
    /**
     * @return
     */
    private String createSignature() {

        String signature = "\n Yours \n";
        signature += "faithfully, the OfCourse Team" ;
        return signature;
    }


    /**
     * @param user
     * @return
     */
    private String createSalutation(User user){
        String header;
        
        if(user.getLastname() == null){
            header = "Dear User" + ", \n\n" ;
        }else{
            header = "Dear " + user.getSalutation() + ". " + user.getLastname() + ", \n\n" ;
        }
        
        return header;
        
    }
    
    /**
     * @param recipients
     * @param CourseUnit
     */
    public void sendCourseUnitDeleteMail(List<String> recipients, int CourseUnit, Transaction transaction){
          
        try{
            CourseUnit editCourseUnit = CourseUnitDAO.getCourseUnit(transaction, CourseUnit);
            
            String subject = "CourseUnit: " +  editCourseUnit.getTitle() + " has been deleted";

            String message = "Dear User, \n \n";
            
            message += "Your CourseUnit:" + editCourseUnit.getTitle() + " at " + editCourseUnit.getStartime() + " has been deleted. \n";
            message += "Please visit the OfCourse WebPage for further Information: \n\n";
            //message += createCourseLink(editCourseUnit.getCourseID()) + "\n";
            message += createSignature();
            
            sendMail(recipients, subject, message);
        
        }catch (InvalidDBTransferException e){
            
            LogHandler.getInstance().error("Error occured during sendCourseUnitEditMail");
        }
            
    
    }
    
    
    
    
    /**
     * @param recipients
     * @param CourseUnit
     */
    public void sendCourseEditUnitMail(List<String> recipients,Transaction transaction, int CourseUnit){
        
        
        try{
            CourseUnit editCourseUnit = CourseUnitDAO.getCourseUnit(transaction, CourseUnit);
            
            String subject = "CourseUnit: " +  editCourseUnit.getTitle() + " has changed";
            
            
            String message = "Dear User, \n \n";
            
            message += "Your CourseUnit:" + editCourseUnit.getTitle() + " at" + editCourseUnit.getStartime() + "has changed. \n";
            message += "Please visit the OfCourse WebPage for further Information: \n\n";
            message += createCourseLink(editCourseUnit.getCourseID()) + "\n";
            message += createSignature();
            
            sendMail(recipients, subject, message);
        
        }catch (InvalidDBTransferException e){
            LogHandler.getInstance().error("Error occured during sendCourseUnitEditMail");
        }
            
    }


    /**
     * @param courseUnit
     * @return
     */
    private String createCourseLink(int courseID) {
        String link = createLink() + "/facelets/open/courses/courseDetail.xhtml?courseID=" + courseID;
        return link;
    }

}