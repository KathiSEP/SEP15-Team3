/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

/**
 * Occurs if a failure happened concerning the database.
 * E.g. timeouts or illegal sql statements.
 *
 * @author Ricky Strohmeier
 *
 */
public class InvalidDBTransferException extends RuntimeException {

    private static final long serialVersionUID = -5414704117180836087L;
    
    /**
     * 
     */
    public InvalidDBTransferException(){
        super("A Error occured during process on Database");
    }
    
    /**
     * @param message
     */
    public InvalidDBTransferException(String message){
        super(message);
    }

}
