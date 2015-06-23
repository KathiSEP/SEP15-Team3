/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.mail.MessagingException;

import de.ofCourse.system.LogHandler;

/**
 * Occurs if there happened a failure concerning the systems mail service.
 * E.g. the server is not reachable or a mail could not be sent.
 *
 * @author Ricky Strohmeier
 *
 */
public class MailingException extends RuntimeException{

    private static final long serialVersionUID = 7556546921801557343L;
    private MessagingException error;

    /**
     * 
     */
    public MailingException(){
        super("A Error occured while sending a Mail. The Problems could be:"
                + "The config input of the Mailserver are not valid or"
                + "the Mail has returned (Not the right Mail address of the"
                + "reciver");
    }
    
    /**
     * @param message
     */
    public MailingException(String message){
        super(message);
        LogHandler.getInstance().error(message);
    }

    /**
     * @param message
     * @param e
     */
    public MailingException(String message, MessagingException e) {
        super(message);
        this.error = e;
        StringWriter errors = new StringWriter();
        error.printStackTrace(new PrintWriter(errors));
        
        LogHandler.getInstance().error(message +"\n\n" + errors.toString());
    }
}
