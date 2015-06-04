/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Checks whether the inserted password fulfills certain security requirements
 * regarding length and choice of signs.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

    /**
     * Gets called when you want to register or change your password when you’re
     * already registered. The method checks if the provided requirements like
     * usage of at least 8 signs, usage of at least one special character, usage
     * of at least one number and usage of lower and upper case. Furthermore
     * it's not allowed to use mutations like 'ß'.
     */
    private Pattern pattern;
    private Matcher matcher;

    private static final String EmailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9.-]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidator() {
	pattern = Pattern.compile(EmailPattern);
    }
    


    @Override
    public void validate(FacesContext arg0, UIComponent component, Object value)
	    throws ValidatorException {
	
	String email = value.toString();
	
	if(email.length() < 1 || email.length() > 319) {
	    throw new ValidatorException(new FacesMessage("Email zu lang."));
	}
	
	matcher = pattern.matcher(email);
	
	if (!matcher.matches()) {
	    throw new ValidatorException(new FacesMessage("Kein gültiges Emailformat."));
	}
    }

}
