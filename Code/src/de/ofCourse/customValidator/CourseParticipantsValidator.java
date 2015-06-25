/**
 * 
 */
package de.ofCourse.customValidator;

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
 * Checks if the inserted number of participants is positive 
 * (0 is also not accepted).
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("courseParticipantsValidator")
public class CourseParticipantsValidator implements Validator {

    /**
     * Gets called when the administrator wants to create a new course and 
     * insert the number of max participants.
     */
    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
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
        
        String maxParticipantsString = value.toString();
        
        int maxParticipants = 1;
        
        try {
            //Check if the insert is a number
            maxParticipants = Integer.parseInt(maxParticipantsString);
        } catch(NumberFormatException e) {
            throw new ValidatorException(
                    new FacesMessage(
                            LanguageManager.getInstance().
                            getProperty(
                                    "createCourse.Validator."
                                    + "CourseParticipants", lang)));
        }
        
        //Check if the insert is a positive number
        if(maxParticipants < 1) {
            throw new ValidatorException(
                    new FacesMessage(
                            LanguageManager.getInstance().
                            getProperty(
                                    "createCourse.Validator."
                                    + "CourseParticipantsNoPositivNumber", 
                                    lang)));
        }
    }

}
