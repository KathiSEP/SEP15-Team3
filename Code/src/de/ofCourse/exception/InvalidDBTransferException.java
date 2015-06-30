/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import de.ofCourse.system.LogHandler;

/**
 * Occurs if a failure happened concerning the database.
 * E.g. timeouts or illegal sql statements.
 *
 * @author Schwarz Sebastian
 *
 */
public class InvalidDBTransferException extends RuntimeException {

    /**
     * serialID
     */
    private static final long serialVersionUID = -5414704117180836087L;
    /**
     * Stores the SQL Exception 
     */
    private Exception error;
    
    /**
     * Calls the Constructor of the parent class
     */
    public InvalidDBTransferException(){
        super("A Error occured during process on Database");
    }
    
    /**
     * Calls the Constructor of the parent class and gives a Log Entry
     * @param message
     */
    public InvalidDBTransferException(String message){
        super(message);
        LogHandler.getInstance().error(message);
    }
    
    
    /**
     * Calls the Constructor of the parent class and gives a Log Entry with StackTrace
     * @param message
     * @param e
     */
    public InvalidDBTransferException(String message, Exception e){
        super(message);
        this.error = e;
        StringWriter errors = new StringWriter();
        error.printStackTrace(new PrintWriter(errors));
        
        LogHandler.getInstance().error(message +"\n\n" + errors.toString());
    }

}
