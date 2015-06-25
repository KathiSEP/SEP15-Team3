/**
 * 
 */
package de.ofCourse.customValidator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
        
        Date startDate = null;
        Date endDate = null;
        
        UIInput uiCourseEndDate = (UIInput) component.getAttributes()
                .get("courseEndDate");
        String courseEndDateString = uiCourseEndDate.getSubmittedValue()
                .toString();
        
        try {
            //Check if the insert has the right format
            startDate = (Date) value;
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            endDate = format.parse(courseEndDateString);
        } catch(Exception e) {
            throw new ValidatorException(
                    new FacesMessage(
                          LanguageManager.getInstance().
                          getProperty(
                             "createCourse.Validator.CourseDateFormat", lang)));
        }
        

        //Check if the starttime is befor the endtime
        if (startDate.getTime() > endDate.getTime()) {
            throw new ValidatorException(
                    new FacesMessage(
                          LanguageManager.getInstance().
                          getProperty(
                                "createCourse.Validator.CourseDateEnd", lang)));
        }
    }

}
