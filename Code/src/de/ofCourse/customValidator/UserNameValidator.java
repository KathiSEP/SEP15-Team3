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

/**
 * Checks whether the entered user name already exists in the system.
 * 
 * @author Katharina H�lzl
 *
 */

@FacesValidator("userNameValidator")
public class UserNameValidator implements Validator {

    /**
     * Gets called when you want to register or change your user name when 
     * you�re already registered. The method checks if the entered user name 
     * already exists in the system.
     */
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
	    throws ValidatorException {
	
	//if(){
	    throw new ValidatorException(new FacesMessage("Dieser Benutzername "
	    	+ "ist bereits vergeben."));
	//}

    }

}
