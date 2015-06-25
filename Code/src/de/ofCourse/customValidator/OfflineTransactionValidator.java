/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Language;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.LanguageManager;

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
	
	Map<String, Object> sessionMap = 
	        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap();

 Language lang = null;
        
        if(sessionMap.containsKey("lang")) {
            lang = Language.fromString(sessionMap.get("lang").toString());
        } else {
            lang = Language.DE;
            HttpSession session = (HttpSession) FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getSession(true);
            session.setAttribute("lang", lang.toString());
        } 
	 
	

	String enteredIDString = value.toString();
	int userID = 0;

	UIInput uiInputUserName = (UIInput) component.getAttributes().get(
		"userNameToTopUp");
	
	String enteredUserName = uiInputUserName.getSubmittedValue().toString();

	// Whether the entered id is a number
	try {
	    userID = Integer.parseInt(enteredIDString);
	} catch (NumberFormatException ex) {
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                            "offlineTransactionValidator.message1", lang)));
	}

	// Whether the entered id is positive
	if (userID < 1) {
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                            "offlineTransactionValidator.message2", lang)));
	}

	Transaction transaction = new Connection();
	transaction.start();

	try {
	    // Whether the id exists
	    if (UserDAO.getUser(transaction, userID) == null) {
		throw new ValidatorException(new FacesMessage(
			LanguageManager.getInstance().getProperty(
	                     "offlineTransactionValidator.message3", lang)));
	    }

	    int fetchedID = UserDAO.getUserID(transaction, enteredUserName);
	    // Whether the username exists
	    if (fetchedID == -1) {
		throw new ValidatorException(new FacesMessage(
			LanguageManager.getInstance().getProperty(
	                     "offlineTransactionValidator.message4", lang)));
	    }
	    // Whether the username and the id belong to the same user
	    if (fetchedID != userID) {
		throw new ValidatorException(new FacesMessage(
			LanguageManager.getInstance().getProperty(
	                     "offlineTransactionValidator.message5", lang)));
	    }

	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                            "userExistsValidator.message4", lang)));
	}
    }

    /**
     * Returns the set language
     */
    private Language getLanguage() {
	Map<String, Object> sessionMap = 
		        FacesContext
	                .getCurrentInstance()
	                .getExternalContext()
	                .getSessionMap();
	
	 Language lang = null;
	        
	        if(sessionMap.containsKey("lang")) {
	            lang = Language.fromString(sessionMap.get("lang").toString());
	        } else {
	            lang = Language.DE;
	            HttpSession session = (HttpSession) FacesContext
	                    .getCurrentInstance()
	                    .getExternalContext()
	                    .getSession(true);
	            session.setAttribute("lang", lang.toString());
	        }        
	return lang;
    }
}
