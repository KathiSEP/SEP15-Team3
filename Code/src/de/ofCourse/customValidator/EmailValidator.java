/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import de.ofCourse.model.Language;
import de.ofCourse.utilities.LanguageManager;

/**
 * Checks whether the inserted e-mail has a correct format.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

    /**
     * Gets called when you want to register, change your e-mail or lost your 
     * password. The method checks if the e-mail has a valid format and if 
     * if it has more than 319 signs.
     */
    private Pattern pattern;
    private Matcher matcher;

    private static final String EmailPattern = "^[_A-Za-z0-9-\\+]+(\\."
            + "[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9.-]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidator() {
	pattern = Pattern.compile(EmailPattern);
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
        
	String email = value.toString();
	
	//Check if the mail has a valid length
	if(email.length() < 1 || email.length() > 319) {
	    throw new ValidatorException(
	            new FacesMessage(
	                    LanguageManager.getInstance().
                            getProperty(
                                   "authenticate.validator.MailLength", lang)));
	}
	
	matcher = pattern.matcher(email);
	
	//Check if the insert has a valid format
	if (!matcher.matches()) {
	    throw new ValidatorException(
	            new FacesMessage(
	                    LanguageManager.getInstance().
                            getProperty(
                               "authenticate.validator.MailFormat", lang)));
	}
    }

}
