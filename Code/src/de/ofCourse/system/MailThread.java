/**
 * 
 */
package de.ofCourse.system;

import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.ofCourse.model.SmtpServer;

/**
 * @author Sebastian Schwarz
 *
 */
@ManagedBean
@RequestScoped
public class MailThread implements Runnable {

    private Properties prop;
    private SmtpServer smtpServer;
    private List<String> recipients;
    private String subject;
    private String message;
    private Authenticator loginAuth;

    /**
     * @param loginAuth
     * @param prop
     * @param session
     * @param recipients
     * @param message
     * @param subject
     */
    public MailThread(Properties prop, Authenticator loginAuth,
            SmtpServer server, List<String> recipients, String subject,
            String message) {
        this.prop = prop;
        this.smtpServer = server;
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
        this.loginAuth = loginAuth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        try {
            // https://javamail.java.net/nonav/docs/api/
            Session session = Session.getDefaultInstance(prop, loginAuth);
            MimeMessage mail = new MimeMessage(session);

            mail.setFrom(new InternetAddress(smtpServer.getUsername()));

            for (String mailAddresse : recipients) {
                mail.setRecipients(Message.RecipientType.BCC, mailAddresse);
            }

            mail.setSubject(subject);
            mail.setText(message);

            Transport transport = session.getTransport("smtp");

            transport.connect(smtpServer.getHostaddr(),
                    smtpServer.getUsername(), smtpServer.getPassword());
            transport.sendMessage(mail, mail.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            //TODO dont know what to do

        }
    }

}
