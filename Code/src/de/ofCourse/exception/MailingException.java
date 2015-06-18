/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

/**
 * Occurs if there happened a failure concerning the systems mail service.
 * E.g. the server is not reachable or a mail could not be sent.
 *
 * @author Ricky Strohmeier
 *
 */
public class MailingException extends RuntimeException{

    private static final long serialVersionUID = 7556546921801557343L;

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
    }
}
