/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.Date;
import java.util.Map;

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
 * Checks whether the inserted date is in the future or more than 150 years in
 * the past.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("dateOfBirthValidator")
public class DateOfBirthValidator implements Validator {

    /**
     * Gets called when you want to register or change your date of birth when
     * you’re already registered. This method checks if the entered date is in
     * the future or more than 150 years in the past.
     */
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object value)
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
        
	Date dateToday = new Date();
	Date dateOfBirth = null;
	
	try {
	    //Check if the insert has a right format
	    dateOfBirth = (Date) value;
	} catch(Exception e) {
	    throw new ValidatorException(
	            new FacesMessage(
	                 LanguageManager.getInstance().
                         getProperty(
                            "authenticate.validator.DateOfBirthFormat", lang)));
	}
	
	    long spread = dateToday.getTime() - dateOfBirth.getTime();
	    long hundredFiftyYears = 4730400000000L;
	    long past = dateToday.getTime() - hundredFiftyYears;

	    //Check if the inserted date is in the future or if its more than 
	    //150 years ago
	    if ((spread < 0) || (dateOfBirth.getTime() < past)) {
		throw new ValidatorException(
		        new FacesMessage(
		              LanguageManager.getInstance().
                              getProperty(
                                "authenticate.validator.DateOfBirth", lang)));
	    }
    }

}
