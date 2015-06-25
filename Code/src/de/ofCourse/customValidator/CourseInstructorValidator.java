/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import de.ofCourse.action.SessionUserBean;
import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Language;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.LanguageManager;

/**
 * Checks whether the entered name of the course instructor exists in the
 * system.
 * 
 * @author Katharina Hölzl
 *
 */
@FacesValidator("courseInstructorValidator")
public class CourseInstructorValidator implements Validator {

    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    /**
     * TGets called when you want to add a course instructor to a course unit or
     * to a course. The method checks if the entered name of the course
     * instructor exists in the system.
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

        String courseLeaderIDString = value.toString();
        int courseLeaderID = 0;

        try {
            //Check it the inserted ID is a number
            courseLeaderID = Integer.parseInt(courseLeaderIDString);
        } catch (NumberFormatException ex) {
            throw new ValidatorException(
                    new FacesMessage("courseLeaderID",
                                    LanguageManager.getInstance().
                                    getProperty(
                                         "createCourse.Validator."
                                         + "CourseInstruktorNoNumber", lang)));
        }

        //Check if the inserted id is a positive number
        if (courseLeaderID < 1) {
            throw new ValidatorException(
                    new FacesMessage("courseLeaderID",
                                    LanguageManager.getInstance().
                                    getProperty(
                                          "createCourse.Validator."
                                          + "CourseInstruktorNoPositivNumber", 
                                          lang)));
        }

        Transaction transaction = new Connection();
        transaction.start();
        try {
            //Check if the course leader id exists in the system
            if (!CourseDAO.courseLeaderExists(transaction, courseLeaderID)) {
                throw new ValidatorException(new FacesMessage("courseLeaderID",
                        LanguageManager.getInstance().
                        getProperty(
                              "createCourse.Validator."
                              + "CourseInstruktorNotExisting", 
                              lang)));
            }
            transaction.commit();
            
        } catch (InvalidDBTransferException e) {
            transaction.rollback();
            throw new ValidatorException(new FacesMessage("courseLeaderID",
                    LanguageManager.getInstance().
                    getProperty(
                          "createCourse.Validator.CourseInstruktorDatabase", 
                          lang)));
        }

    }

}
