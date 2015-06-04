/**
 * 
 */
package de.ofCourse.customValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Checks whether the entered user name already exists in the system.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("userNameValidator")
public class UserNameValidator implements Validator {

    /**
     * Gets called when you want to register or change your user name when 
     * you’re already registered. The method checks if the entered user name 
     * already exists in the system.
     */
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object value)
	    throws ValidatorException {
	
	String username = value.toString();
	
	if(username.length() < 5 || username.length() > 100) {
	    throw new ValidatorException(new FacesMessage("Der Benutzername muss zwischen 5 und 100 Zeichen lang sein."));
	}
	
	Transaction transaction = new Connection();
	transaction.start();
	int id = UserDAO.getUserID(transaction, username);
	
	if(id != -1){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage("Dieser Benutzername "
	    	+ "ist bereits vergeben.");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            facesContext.addMessage(null, msg);
            facesContext.renderResponse();
	    throw new ValidatorException(new FacesMessage("Dieser Benutzername "
	    	+ "ist bereits vergeben."));
	}

    }

}
