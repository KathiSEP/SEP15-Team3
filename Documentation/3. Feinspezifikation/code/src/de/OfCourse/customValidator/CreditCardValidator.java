/**
 * 
 */
package de.OfCourse.customValidator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class checks wether the credit card number ist valid.
 * 
 * @author Kathi
 *
 */
public class CreditCardValidator implements Validator {


    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
	    throws ValidatorException {
	// TODO Auto-generated method stub

    }

}
