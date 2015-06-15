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
        
        String maxParticipantsString = value.toString();
        
        int maxParticipants = 1;
        
        try {
            maxParticipants = Integer.parseInt(maxParticipantsString);
        } catch(NumberFormatException e) {
            throw new ValidatorException(new FacesMessage("Keine gültige "
                    + "Anzahl an Teilnehmern."));
        }
        
        if(maxParticipants < 1) {
            throw new ValidatorException(new FacesMessage("Teilnehmerzahl muss "
                    + "positiv sein!"));
        }
    }

}
