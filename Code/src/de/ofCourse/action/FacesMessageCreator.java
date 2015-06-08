package de.ofCourse.action;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesMessageCreator {
    /**
     * Creates a new FacesMessage with the given message.
     * 
     * @param component
     *            binding of the message to the component
     * @param message
     *            message to display
     */
    public static void createFacesMessageWithMessage(String component, String message) {

	FacesContext facesContext = FacesContext.getCurrentInstance();
	FacesMessage msg = new FacesMessage(message);
	msg.setSeverity(FacesMessage.SEVERITY_INFO);
	facesContext.addMessage(component, msg);
	facesContext.renderResponse();
    }

    /**
     * Creates the FacesMessage with the given exception.
     * 
     * @param component
     *            binding of the message to the component
     * @param e
     *            exception which was thrown
     */
    public static void createFacesMessageWithException(String component, Exception e) {

	FacesContext facesContext = FacesContext.getCurrentInstance();
	FacesMessage msg = new FacesMessage(e.getMessage());
	msg.setSeverity(FacesMessage.SEVERITY_INFO);
	facesContext.addMessage(component, msg);
	facesContext.renderResponse();

    }
}
