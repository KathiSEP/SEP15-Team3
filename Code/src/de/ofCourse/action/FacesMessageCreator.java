package de.ofCourse.action;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 * Creates faces messages.
 * 
 * @author Tobias Fuchs, Kathi Hölzl
 *
 */
public class FacesMessageCreator {
    /**
     * Creates a new FacesMessage with the given message.
     * 
     * @param component
     *            binding of the message to the component
     * @param message
     *            message to display
     */
    public static void createFacesMessage(String component, String message) {
        createFacesMessage(component, message, FacesMessage.SEVERITY_INFO);
    }

    /**
     * Creates the FacesMessage with the given exception.
     * 
     * @param component
     *            binding of the message to the component
     * @param e
     *            exception which was thrown
     */
    public static void createFacesMessage(String component, Exception e) {
        createFacesMessage(component, e.getMessage(),
                FacesMessage.SEVERITY_INFO);
    }

    /**
     * Creates the FacesMessage with the given message of the exception and the
     * given severity.
     * 
     * @param component
     *            binding of the message to the component
     * @param e
     *            exception which was thrown
     * @param facesSeverity
     *            severity of the message
     */
    public static void createFacesMessage(String component, Exception e,
            Severity facesSeverity) {
        createFacesMessage(component, e.getMessage(), facesSeverity);
    }

    /**
     * Creates the FacesMessage with the given message and the given severity.
     * 
     * @param component
     *            binding of the message to the component
     * @param message
     *            text of the message
     * @param facesSeverity
     *            severity of the message
     */
    public static void createFacesMessage(String component, String message,
            Severity facesSeverity) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(message);
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        facesContext.addMessage(component, msg);
        facesContext.renderResponse();
    }
}
