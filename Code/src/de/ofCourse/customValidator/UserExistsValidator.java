package de.ofCourse.customValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

public class UserExistsValidator implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
	    throws ValidatorException {

	String userIDString = value.toString();
	int userID = 0;

	try {
	    userID = Integer.parseInt(userIDString);
	} catch (NumberFormatException ex) {
	    throw new ValidatorException(new FacesMessage("userID",
		    "Eingegebene ID ist keine Zahl!"));
	}

	if (userID < 1) {
	    throw new ValidatorException(new FacesMessage("userID",
		    "Eingegebene ID muss eine positive Zahl sein."));
	}

	Transaction transaction = new Connection();
	transaction.start();
	try {
	    if (UserDAO.getUser(transaction, userID )== null) {
		throw new ValidatorException(new FacesMessage("userID",
			"Eingegebene ID existiert nicht im System."));
	    }
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    throw new ValidatorException(new FacesMessage("courseLeaderID",
		    "Ein interner Datenbankfehler trat auf."));
	}

    }

}
