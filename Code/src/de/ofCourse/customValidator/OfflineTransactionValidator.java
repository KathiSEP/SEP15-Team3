/**
 * 
 */
package de.ofCourse.customValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.ofCourse.database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Checks in case of offline charging of an users credit, whether the entered
 * name and the entered user id belong to the same user and if the id is
 * existing in the database.
 * 
 * @author Tobias Fuchs
 *
 */
public class OfflineTransactionValidator implements Validator {

    /**
     * Gets called when you want to charge the account of an registered user
     * with credits. The method checks if the inserted name and the entered user
     * id belong to the same user. Furthermore it checks, if the id is existing
     * in the database.
     */
    @Override
    public void validate(FacesContext fctx, UIComponent component, Object value)
	    throws ValidatorException {

	String enteredIDString = value.toString();
	System.out.println("enteredId :" + enteredIDString);
	int userID = 0;

	UIInput uiInputUserName = (UIInput) component.getAttributes().get(
		"userNameToTopUp");
	
	String enteredUserName = uiInputUserName.getSubmittedValue().toString();

	// Whether the entered id is a number
	try {
	    userID = Integer.parseInt(enteredIDString);
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

	try {
	    // Whether the id exists
	    if (UserDAO.getUser(transaction, userID) == null) {
		throw new ValidatorException(new FacesMessage("userID",
			"Eingegebene ID existiert nicht im System."));
	    }

	    int fetchedID = UserDAO.getUserID(transaction, enteredUserName);
	    // Whether the username exists
	    if (fetchedID == -1) {
		throw new ValidatorException(new FacesMessage("userID",
			"Eingegebener Benutzername existiert nicht im System."));
	    }
	    // Whether the username and the id belong to the same user
	    if (fetchedID != userID) {
		throw new ValidatorException(new FacesMessage("userID",
			"Die eingegebene ID und der Benutzername"
				+ " gehören nicht zum selben Benutzer."));
	    }

	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    throw new ValidatorException(new FacesMessage("courseLeaderID",
		    "Ein interner Datenbankfehler trat auf."));
	}
    }
}
