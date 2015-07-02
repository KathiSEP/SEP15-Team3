package de.ofCourse.customValidator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import de.ofCourse.model.Language;
import de.ofCourse.utilities.LanguageManager;

/**
 * Checks whether the entered amount of money has the correct format
 * 
 * @author Tobias Fuchs
 *
 */
@FacesValidator("priceValidator")
public class PriceValidator implements Validator {

    /**
     * Validates entered price for correct format
     */
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object price)
	    throws ValidatorException {
	
	 Language lang = getLanguage();
	 String priceAsString = "" + price;
	 
	 if(!priceAsString.matches("[0-9][0-9]*([.][0-9])?")){
	     throw new ValidatorException(
		            new FacesMessage(
		                 LanguageManager.getInstance().
	                         getProperty(
	                            "converterMessage.money2", lang)));
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
