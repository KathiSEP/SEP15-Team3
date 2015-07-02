package de.ofCourse.customValidator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import de.ofCourse.model.Language; 
import de.ofCourse.utilities.LanguageManager;
 
public class MinMaxValidator implements Validator {

    @Override
    public void validate(FacesContext arg0, UIComponent component, Object value)
	    throws ValidatorException {
	Language lang = getLanguage();
	System.out.println("FSDF");
	
	String minUsersAsString = value.toString();
	
	UIInput uiInputMaxUsers= (UIInput) component.getAttributes().get(
		"maxUsersCourseUnit");
	
	String enteredMaxUsers = uiInputMaxUsers.getSubmittedValue().toString();
	
	int minUsers;
	int maxUsers;
	try {
	    
	minUsers = Integer.parseInt(minUsersAsString);
	maxUsers = Integer.parseInt(enteredMaxUsers);
	
	} catch (NumberFormatException ex) {
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                            "offlineTransactionValidator.message1", lang)));
	}
	
	if(!(minUsers >= maxUsers)){
	    throw new ValidatorException(new FacesMessage(
		    LanguageManager.getInstance().getProperty(
                            "offlineTransactionValidator.message1", lang)));
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
