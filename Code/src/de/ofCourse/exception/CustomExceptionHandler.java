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
 * @author Ricky Strohmeier
 *
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private ExceptionHandler wrapped;
    Iterator<ExceptionQueuedEvent> exceptionList;
    
    /**
     * 
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
           ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
           Throwable thr = context.getException();

           if ( thr.getMessage() == "java.lang.NullPointerException" ) {
              redirectToDefault();              
            } else if( thr.getMessage().contains("de.ofCourse.exception.CourseRegistrationException")){
                redirectToCourseRegistration(thr);  
            } else {
              redirectTo404();  
            }
           getWrapped().handle();
        }
        
     }


    /**
     * 
     */
    private void redirectToCourseRegistration(Throwable thr) {
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nav = fc.getApplication().getNavigationHandler();
        try {
           fc.addMessage(null, new FacesMessage("Dear User that shouldnt have happened") );
           nav.handleNavigation(fc, null, "/facelets/ErrorPages/CourseRegistrationException.xthml?faces-redirect=true" );
           fc.renderResponse();
        } finally {
           this.exceptionList.remove();
        }
    }


    /**
     * @param exceptionList
     */
    private void redirectTo404() {
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nav = fc.getApplication().getNavigationHandler();
        try {
           fc.addMessage( null, new FacesMessage("Dear User that shouldnt have happened") );
           nav.handleNavigation(fc, null, "/facelets/ErrorPages/404.xthml?faces-redirect=true" );
           fc.renderResponse();
        } finally {
           this.exceptionList.remove();
        }
  
        
    }


    /**
     * @param exceptionList
     */
    private void redirectToDefault() {
        FacesContext fc = FacesContext.getCurrentInstance();
          NavigationHandler nav = fc.getApplication().getNavigationHandler();
          try {
             fc.addMessage( null , new FacesMessage("Dear User that shouldnt have happened") );
             nav.handleNavigation(fc, null , "/facelets/ErrorPages/default.xthml?faces-redirect=true" );
             fc.renderResponse();
          } finally {
             exceptionList.remove();
          }
    }
    
}
