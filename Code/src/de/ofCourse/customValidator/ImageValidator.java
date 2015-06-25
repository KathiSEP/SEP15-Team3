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
import javax.servlet.http.Part;

import de.ofCourse.model.Language;
import de.ofCourse.utilities.LanguageManager;

/**
 * Checks whether the image file has the right file extension and whether some 
 * other conditions like maximum file and permitted resolution of the image 
 * (logo or image of a course) are kept.
 * 
 * @author Katharina Hölzl
 *
 */
@FacesValidator("imageValidator")
public class ImageValidator implements Validator {

  
    /**
     * Gets called when you want to change the logo or the image of a course. 
     * The method checks if the image file has the right file extension, in 
     * this case .jpg. Furthermore it checks, if the conditions of the maximum 
     * file and permitted resolution of the image are kept.
     *  
     */
    
    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
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
    
        Part image = (Part) value;
        
        //Check if the image type is jpeg
        if (!image.getContentType().equals("image/jpeg")) {
            throw new ValidatorException(new FacesMessage(LanguageManager.
                    getInstance().
                    getProperty(
                            "createCourse.Validator.ImageFormat", lang)));
        }
        //Check if the size of the image is smaller than 100 KB
        if(image.getSize() > 102400) {
            throw new ValidatorException(new FacesMessage(LanguageManager.
                    getInstance().
                    getProperty(
                            "createCourse.Validator.ImageSize", lang)));
        }
    }
}
