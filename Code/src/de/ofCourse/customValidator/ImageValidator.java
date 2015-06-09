/**
 * 
 */
package de.ofCourse.customValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Checks whether the image file has the right file extension and whether some 
 * other conditions like maximum file and permitted resolution of the image 
 * (logo or image of a course) are kept.
 * 
 * @author Katharina Hölzl
 *
 */
public class ImageValidator implements Validator {

  
    /**
     * Gets called when you want to change the logo or the image of a course. 
     * The method checks if the image file has the right file extension, in 
     * this case .jpg. Furthermore it checks, if the conditions of the maximum 
     * file and permitted resolution of the image are kept.
     *  
     */
    
    private Pattern pattern;
    private Matcher matcher;

    private static final String ImagePattern = "([^\\s]+(\\.(?i)(jpg))$)";

    public ImageValidator() {
        pattern = Pattern.compile(ImagePattern);
    }
    
    @Override
    public void validate(FacesContext fc, UIComponent component, Object value)
	    throws ValidatorException {
        
    
        String image = value.toString();

        matcher = pattern.matcher(image);
        
        if (!matcher.matches()) {
            throw new ValidatorException(new FacesMessage("Kein gültiges Bildformat."));
    }
    }
}
