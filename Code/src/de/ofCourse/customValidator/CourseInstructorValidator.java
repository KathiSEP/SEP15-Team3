/**
 * 
 */
package de.ofCourse.customValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Checks whether the entered name of the course instructor exists in the
 * system.
 * 
 * @author Katharina Hölzl
 *
 */
public class CourseInstructorValidator implements Validator {

    /**
     * TGets called when you want to add a course instructor to a course unit or
     * to a course. The method checks if the entered name of the course
     * instructor exists in the system.
     */
    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
            throws ValidatorException {

        String courseLeaderIDString = value.toString();
        int courseLeaderID = 0;

        try {
            courseLeaderID = Integer.parseInt(courseLeaderIDString);
        } catch (NumberFormatException ex) {
            throw new ValidatorException(new FacesMessage("courseLeaderID",
                    "Eingegebene Kursleiter-ID ist keine Zahl!"));
        }

        if (courseLeaderID < 1) {
            throw new ValidatorException(new FacesMessage("courseLeaderID",
                    "Eingegebene Kursleiter-ID muss eine positive Zahl sein."));
        }

        Transaction transaction = new Connection();
        transaction.start();
        try {
            if (!CourseDAO.courseLeaderExists(transaction, courseLeaderID)) {
                throw new ValidatorException(new FacesMessage("courseLeaderID",
                        "Eingegebene Kursleiter-ID existiert nicht im System."));
            }
            transaction.commit();
        } catch (InvalidDBTransferException e) {
            transaction.rollback();
            throw new ValidatorException(new FacesMessage("courseLeaderID",
                    "Ein interner Datenbankfehler trat auf."));
        }

    }

}
