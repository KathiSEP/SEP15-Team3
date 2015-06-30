/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import de.ofCourse.system.LogHandler;

/**
 * Occurs if a failure happened during the registration process for a course.
 * The CourseRegistrationException is a runtime exception.
 *
 * @author Schwarz Sebatian
 *
 */
public class CourseRegistrationException extends RuntimeException{

    /**
     * serialID
     */
    private static final long serialVersionUID = -783634204496326525L;
    /**
     * Stores the SQL Exception 
     */
    private Exception error;
    
    
    /**
     * calls the Constructer of the parent Class
     */
    public CourseRegistrationException(){
        super("A Error occured during signing up for a Course/CourseUnit. "
                + "The problems could be: "
                + "The Course/CourseUnit reached their maximal amount of participants"
                + " or you dont have enough money to sign up for the courseUnit");
    }

    /**
     * calls the Constructer of the parent Class and gives a Log Entry
     */
    public CourseRegistrationException(String message){
        super(message);
        LogHandler.getInstance().error(message);
    }
    
    /**
     * calls the Constructer of the parent Class and gives a Log Entry with Stack Trace
     */
    public CourseRegistrationException(String message, Exception e){
        super(message);
        this.error = e;
        StringWriter errors = new StringWriter();
        error.printStackTrace(new PrintWriter(errors));
        
        LogHandler.getInstance().error(message +"\n\n" + errors.toString());
    }
}
