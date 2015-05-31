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
 * Checks whether the two inserted passwords are equal.
 * 
 * @author Katharina H�lzl
 *
 */

@FacesValidator("confirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator {

    /**
     * Gets called when you want to register or change your password when 
     * you�re already registered. Therefore you have to insert your password 
     * twice due to security issues. This method checks if the two 
     * passwords are equal.
     */
    
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
	    throws ValidatorException {

	//if(){
	    throw new ValidatorException(new FacesMessage("Die beiden "
	    	+ "eingegebenen Passw�rter stimmen nicht �berein."));
	//}

    }

}
