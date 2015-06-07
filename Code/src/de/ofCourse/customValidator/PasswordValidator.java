/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Checks whether the inserted password fulfills certain security requirements
 * regarding length and choice of signs and if the two insertet passwords are 
 * equal.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    /**
     * Gets called when you want to register or change your password when you’re
     * already registered. The method checks if the provided requirements like
     * usage of at least 8 signs, usage of at least one special character, usage
     * of at least one number and usage of lower and upper case. Furthermore
     * it's not allowed to use mutations like 'ß'. On top of that it checks if
     * the two inserted passwords are equal.
     */

    private Pattern pattern;
    private Matcher matcher;

    private static final String PasswordPattern = "((?=.*\\d)(?=.*[a-z])"
	    + "(?=.*[A-Z])(?=.*[@#$%!_?&]).{8,100})";

    public PasswordValidator() {
	pattern = Pattern.compile(PasswordPattern);
    }

    @Override
    public void validate(FacesContext arg0, UIComponent component, Object value)
	    throws ValidatorException {
	
	String password = value.toString();

	UIInput uiInputConfirmPassword = (UIInput) component.getAttributes()
		.get("confirmPassword");
	String confirmPassword = uiInputConfirmPassword.getSubmittedValue()
		.toString();

	    if(!confirmPassword.equals(password)){
		throw new ValidatorException(new FacesMessage("Passwörter müssen übereinstimmen."));
	    }
	
	
	if(password.length() < 8 || password.length() > 100) {
	    throw new ValidatorException(
		    new FacesMessage(
			    "Das Passwort muss mindestens 8 Zeichen lang sein, darf aber höchstens 100 Zeichen lang sein."));
	}
	
	matcher = pattern.matcher(password);
	
	if (!matcher.matches()) {
	    throw new ValidatorException(
		    new FacesMessage(
			    "Das Passwort muss mindestens "
			    + "ein Sonderzeichen (@,#,$,%,!,_,?,&), Ziffern und Groß- und "
			    + "Kleinbuchstaben enthalten. ß, ä, ö, ü sind "
			    + "nicht erlaubt."));
	}

    }

}
