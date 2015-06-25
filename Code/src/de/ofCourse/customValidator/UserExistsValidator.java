package de.ofCourse.customValidator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
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
	
	Language lang = getLanguage();
	
	String userIDString = value.toString();
	int userID = 0;
	
	// Whether the entered id is a number
	try {
	    userID = Integer.parseInt(userIDString);
	} catch (NumberFormatException ex) {
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                          "userExistsValidator.message1", lang)));
	}
	
	// Whether the entered id is positive
	if (userID < 1) {
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                          "userExistsValidator.message2", lang)));
	}

	Transaction transaction = new Connection();
	transaction.start();

	// Whether the entered id is a valid id
	try {
	    if (UserDAO.getUser(transaction, userID) == null) {
		throw new ValidatorException(new FacesMessage(
			LanguageManager.getInstance().getProperty(
                            "userExistsValidator.message3", lang)));
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
