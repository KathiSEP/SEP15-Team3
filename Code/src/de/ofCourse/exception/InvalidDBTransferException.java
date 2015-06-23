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
 * @author Ricky Strohmeier
 *
 */
public class InvalidDBTransferException extends RuntimeException {

    private static final long serialVersionUID = -5414704117180836087L;
    private SQLException error;
    
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
        LogHandler.getInstance().error(message);
    }
    
    
    public InvalidDBTransferException(String message, SQLException e){
        super(message);
        this.error = e;
        StringWriter errors = new StringWriter();
        error.printStackTrace(new PrintWriter(errors));
        
        LogHandler.getInstance().error(message +"\n\n" + errors.toString());
    }

}
