/**
 * 
 */
package de.ofCourse.system;

import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.mail.Authenticator;
import javax.mail.Session;

import de.ofCourse.model.SmtpServer;

/**
 * @author Sebastian Schwarz
 *
 */
@ManagedBean
@RequestScoped
public abstract class MailThread implements Runnable{
    
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
    public MailThread(Properties prop, Authenticator loginAuth, SmtpServer server, List<String> recipients, String subject, String message) {
        this.prop = prop;
        this.smtpServer = server;
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
        this.loginAuth = loginAuth;
    }

}
