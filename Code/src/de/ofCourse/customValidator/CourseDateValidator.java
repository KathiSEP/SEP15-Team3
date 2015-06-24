/**
 * 
 */
package de.ofCourse.customValidator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Checks if the inserted start date is before the inserted end date.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("courseDateValidator")
public class CourseDateValidator implements Validator {

    /**
     * Gets called for example when a new course is created and checks if the 
     * inserted start date is before the inserted end date.
     */
    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
            throws ValidatorException {
        Date startDate = null;
        Date endDate = null;
        
        UIInput uiCourseEndDate = (UIInput) component.getAttributes()
                .get("courseEndDate");
        String courseEndDateString = uiCourseEndDate.getSubmittedValue()
                .toString();
        
        try {
            startDate = (Date) value;
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            endDate = format.parse(courseEndDateString);
        } catch(Exception e) {
            throw new ValidatorException(new FacesMessage("Datum muss im "
                    + "Format dd.MM.yyyy angegeben werden."));
        }
        

        if (startDate.getTime() > endDate.getTime()) {
            throw new ValidatorException(new FacesMessage("Startdatum muss vor "
                    + "dem Enddatum liegen."));
        }
    }

}
