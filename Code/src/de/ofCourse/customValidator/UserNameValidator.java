/**
 * 
 */
package de.ofCourse.customValidator;


import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
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
        
        String username = value.toString();

        //Check if the insert has a valid length
        if (username.length() < 5 || username.length() > 100) {
            throw new ValidatorException(
                    new FacesMessage(LanguageManager.getInstance().
                            getProperty(
                               "authenticate.validator.UserNameLength", lang)));
        }

        Transaction transaction = new Connection();
        transaction.start();
        try {
            //check if the username is already existing in the system
            int id = UserDAO.getUserID(transaction, username);
            transaction.commit();
            if (id != -1) {
                throw new ValidatorException(
                     new FacesMessage(LanguageManager.getInstance().
                            getProperty(
                                     "authenticate.validator.UserName", lang)));
            }
        } catch (InvalidDBTransferException e) {
            transaction.rollback();
        }

    }

}
