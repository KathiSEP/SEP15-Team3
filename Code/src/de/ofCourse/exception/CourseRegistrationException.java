/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

/**
 * Occurs if a failure happened during the registration process for a course.
 * The CourseRegistrationException is a runtime exception.
 *
 * @author Ricky Strohmeier
 *
 */
public class CourseRegistrationException extends RuntimeException{

    private static final long serialVersionUID = -783634204496326525L;
    
    /**
     * 
     */
    public CourseRegistrationException(){
        super("A Error occured during signing up for a Course/CourseUnit. "
                + "The problems could be: "
                + "The Course/CourseUnit reached their maximal amount of participants"
                + " or you dont have enough money to sign up for the courseUnit");
    }

    public CourseRegistrationException(String message){
        super(message);
    }
}
