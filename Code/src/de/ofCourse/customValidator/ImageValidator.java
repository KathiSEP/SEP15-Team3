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
        
        Map<String, Object> sessionMap = FacesContext
                .getCurrentInstance().getExternalContext().getSessionMap();
        
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
    
        Part image = (Part) value;
        
        System.out.println(image.getContentType());
        
        if (!image.getContentType().equals("image/jpeg")) {
            throw new ValidatorException(new FacesMessage(LanguageManager.getInstance().
                    getProperty(
                            "createCourse.Validator.ImageFormat", lang)));
        }
        if(image.getSize() > 102400) {
            throw new ValidatorException(new FacesMessage(LanguageManager.getInstance().
                    getProperty(
                            "createCourse.Validator.ImageSize", lang)));
        }
    }
}
