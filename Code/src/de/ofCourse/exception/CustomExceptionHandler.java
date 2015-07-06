/**
 * Contains all custom exceptions that may occur in the OfCourse system.
 */
package de.ofCourse.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * Handles all occuring exceptions concerning the OfCourse system.
 *
 * @author Schwarz Sebastian
 *
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    /**
     * Stores a Attribut of ExceptionHandler
     */
    private ExceptionHandler wrapped;
    
    /**
     * A List of all exceptions
     */
    Iterator<ExceptionQueuedEvent> exceptionList;
    
    /**
     *  initialize the attribute
     */
    public CustomExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }
    

    /**
	 * Wraps the exception handler.
	 *
	 * @return the instance of the wrapped exception handler
	 *
	 */
    @Override
    public ExceptionHandler getWrapped() {

	return wrapped;
    }

	/**
	 * Calls the <code>handle</code> method from <code>wrapped</code>.
	 *
	 * @throws FacesException if an error occurs while the algorithm to handle the exception occurs.
	 *
	 */
    @Override
    public void handle() throws FacesException {
        exceptionList = getUnhandledExceptionQueuedEvents().iterator();
        while ( exceptionList.hasNext() ) {
    
           ExceptionQueuedEvent event = exceptionList.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event
                    .getSource();
            Throwable thr = context.getException();

            while (thr != null) {
                if (thr instanceof java.lang.RuntimeException) {
                    thr = thr.getCause();
                    redirectToDefault();
                } else {
                    thr = thr.getCause();
                    break;
                }
            }

            redirectTo404();

        }
        
     }



    /**
     * Redirects to the 404 Error Page
     * 
     * @param exceptionList
     */
    private void redirectTo404() {
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nav = fc.getApplication().getNavigationHandler();
        
           fc.addMessage( null, new FacesMessage("Dear User that shouldnt have happened") );
           nav.handleNavigation(fc, null, "/facelets/ErrorPages/404.xthml" );
           fc.renderResponse();
        
        
    }


    /**
     * Redirect to the Default Error Page
     * 
     * @param exceptionList
     */
    private void redirectToDefault() {
        FacesContext fc = FacesContext.getCurrentInstance();
          NavigationHandler nav = fc.getApplication().getNavigationHandler();
         
             fc.addMessage( null , new FacesMessage("Dear User that shouldnt have happened") );
             nav.handleNavigation(fc, null , "/facelets/ErrorPages/default.xthml" );
             fc.renderResponse();
         
    }
    
}
