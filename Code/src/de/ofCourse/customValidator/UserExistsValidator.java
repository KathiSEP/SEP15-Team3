package de.ofCourse.customValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.ofCourse.database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Checks whether the inserted user id is a positive number. Furthermore it is
 * checked whether the entered id is a existing id.
 * 
 * @author Tobias Fuchs
 *
 */
public class UserExistsValidator implements Validator {

    /**
     * Checks whether the entered id is a positive number and belongs to a
     * existing user.
     */
    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
	    throws ValidatorException {

	String userIDString = value.toString();
	int userID = 0;
	// Whether the entered id is a number
	try {
	    userID = Integer.parseInt(userIDString);
	} catch (NumberFormatException ex) {
	    throw new ValidatorException(new FacesMessage("userID",
		    "Eingegebene ID ist keine Zahl!"));
	}
	// Whether the entered id is positive
	if (userID < 1) {
	    throw new ValidatorException(new FacesMessage("userID",
		    "Eingegebene ID muss eine positive Zahl sein."));
	}

	Transaction transaction = new Connection();
	transaction.start();

	// Whether the entered id is a valid id
	try {
	    if (UserDAO.getUser(transaction, userID) == null) {
		throw new ValidatorException(new FacesMessage("userID",
			"Eingegebene ID existiert nicht im System."));
	    }
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    throw new ValidatorException(new FacesMessage("userID",
		    "Ein interner Datenbankfehler trat auf."));
	}
    }
}
