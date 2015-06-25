/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Initializes a <code>ExceptionHandler</code> which handles all occuring exceptions.
 *
 * @author Schwarz Sebastian
 *
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory{

    /**
     * Stores a instance of ExcpetionHandlerFactory
     */
    private ExceptionHandlerFactory parent;
    
    /**
     * Initializes the attribute of CostumExceptionHandlerFactory
     */
    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }
    
	/**
	 * Returns an <code>ExceptionHandler</code> Object which handles the occured exception.
	 *
	 * @return the initialized <code>ExceptionHandler</code>
	 */
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = parent.getExceptionHandler();
        result = new CustomExceptionHandler(result);
        return result;
    }

}
