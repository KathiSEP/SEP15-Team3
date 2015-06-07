/**
 * 
 */
package de.ofCourse.customValidator;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Checks whether the entered user name already exists in the system and if it
 * has a length between 5 and 100 signs.
 * 
 * @author Katharina Hölzl
 *
 */

@FacesValidator("userNameValidator")
public class UserNameValidator implements Validator {

    /**
     * Gets called when you want to register or change your user name when
     * you’re already registered. The method checks if the entered user name
     * already exists in the system and if it's length is between 5 and 100
     * signs.
     */
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object value)
            throws ValidatorException {

        String username = value.toString();

        if (username.length() < 5 || username.length() > 100) {
            throw new ValidatorException(
                    new FacesMessage(
                            "Der Benutzername muss zwischen 5 und 100 Zeichen lang sein."));
        }

        Transaction transaction = new Connection();
        transaction.start();
        try {
            int id = UserDAO.getUserID(transaction, username);
            transaction.commit();
            if (id != -1) {
                throw new ValidatorException(new FacesMessage(
                        "Dieser Benutzername " + "ist bereits vergeben."));
            }
        } catch (InvalidDBTransferException e) {
            transaction.rollback();
        }

        

    }

}
