/**
 * 
 */
package de.ofCourse.customValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Checks whether the two inserted passwords are equal.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("confirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator {

    /**
     * Gets called when you want to register or change your password when 
     * you’re already registered. Therefore you have to insert your password 
     * twice due to security issues. This method checks if the two 
     * passwords are equal.
     */
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
	    throws ValidatorException {
	String password = value.toString();
	
	UIInput uiInputVerifyPassword = (UIInput) component.getAttributes()
		.get("passwordRegister");

	if(uiInputVerifyPassword == null) {
	    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "sameError.", "Bitte bestätigen Sie"
	    	+ "Ihr Passwort durch erneute Eingabe!"));
	} else {
            String verifyPassword = uiInputVerifyPassword.getSubmittedValue()
                    .toString();
        	
	    if(!password.equals(verifyPassword)){
		throw new ValidatorException(new FacesMessage());
	    }
	}

    }

}
