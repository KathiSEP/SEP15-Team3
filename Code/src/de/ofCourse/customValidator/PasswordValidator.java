/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import de.ofCourse.model.Language;
import de.ofCourse.utilities.LanguageManager;

/**
 * Checks whether the inserted password fulfills certain security requirements
 * regarding length and choice of signs and if the two inserted passwords are 
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
	    + "(?=.*[A-Z])(?=.*[@#$%!_?*&]).{8,100})";

    public PasswordValidator() {
	pattern = Pattern.compile(PasswordPattern);
    }

    @Override
    public void validate(FacesContext arg0, UIComponent component, Object value)
	    throws ValidatorException {
	
        //Fetch the parameter language with the value DE, EN ore BAY out of the 
        //Session Map
        Map<String, Object> sessionMap = FacesContext
                .getCurrentInstance().getExternalContext().getSessionMap();
        
        Language lang = null;
        
        //Check if the parameter language exists in the session.
        if(sessionMap.containsKey("lang")) {
            //Convert the string (DE, EN, BAY) into a language object
            lang = Language.fromString(sessionMap.get("lang").toString());
            
        } else {
            //Set the language to German (DE) an write the parameter into the 
            //session
            lang = Language.DE;
            HttpSession session = (HttpSession) FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getSession(true);
            session.setAttribute("lang", lang.toString());
        }
        
	String password = value.toString();

	UIInput uiInputConfirmPassword = (UIInput) component.getAttributes()
		.get("confirmPassword");
	String confirmPassword = uiInputConfirmPassword.getSubmittedValue()
		.toString();

	//Check if the two inserted passwords are equal
	if(!confirmPassword.equals(password)){
		throw new ValidatorException(
		        new FacesMessage(
		             LanguageManager.getInstance().
	                     getProperty(
	                       "authenticate.validator.PasswordEquals", lang)));
	 }
	
	//Check if the inserted password has a valid length
	if(password.length() < 8 || password.length() > 100) {
	    throw new ValidatorException(
		    new FacesMessage(
		            LanguageManager.getInstance().
                            getProperty(
                              "authenticate.validator.PasswordLength", lang)));
	}
	
	matcher = pattern.matcher(password);
	
	//Check if the format is valid
	if (!matcher.matches()) {
	    throw new ValidatorException(
		    new FacesMessage(
		            LanguageManager.getInstance().
                            getProperty(
                              "authenticate.validator.PasswordPattern", lang)));
	}

    }

}
