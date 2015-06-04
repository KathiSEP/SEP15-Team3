package de.ofCourse.action;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import de.ofCourse.model.Address;
import de.ofCourse.model.User;

public class RegisterUserBeanTest {

    @Test
    public void test() {
	RegisterUserBean registerUserBean = new RegisterUserBean();
	
	User user = new User();
	Address address = new Address();
	
	user.setFirstname("Kathi");
	user.setLastname("Hölzl");
	user.setUsername("Kathi1");
	user.setDateOfBirth(new Date(1234567890));
	user.setEmail("katharina_hoelzl@web.de");
	user.setAccountBalance(0);
	
	String saluString = "MS";
	String password = "Test123#";
	
	address.setCity("Vilshofen");
	address.setCountry("Deutschland");
	address.setHouseNumber(3);
	address.setStreet("Baumgasse");
	address.setZipCode(94474);
	
	user.setAddress(address);

	registerUserBean.setUserToRegistrate(user);
	
	assertEquals(user, registerUserBean.getUserToRegistrate());
	
	registerUserBean.setRegisterPassword(password);
	registerUserBean.setRegisterConfirmPassword(password);
	
	assertEquals(password, registerUserBean.getRegisterPassword());
	assertEquals(password, registerUserBean.getRegisterConfirmPassword());
	
	registerUserBean.setSaluString(saluString);
	
	assertEquals(registerUserBean.registerUser(), "/facelets/open/index.xhtml?faces-redirect=false");
    }

}
