package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.model.Address;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.Salutation;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * 
 * @author Patrick Cretu
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, UserDAO.class,
	FacesContext.class, })
public class UserProfileBeanTest {

	private boolean readOnly;
	
	private String password;
	
	private Connection conn;
	
	private String salutation;
	
	private String role;
	
	private User user;
	
	private User checkUser;
	
	private UserProfileBean bean;
	
	// RequestparameterMap
    private Map<String, String> pm;
	
	@Before
    public void setup() {
		// Mock the FacesContext
		PowerMockito.mockStatic(FacesContext.class);
		FacesContext fc = mock(FacesContext.class);
		Mockito.when(FacesContext.getCurrentInstance()).thenReturn(fc);
		
		// Mock the ExternalContext
		ExternalContext ec = mock(ExternalContext.class);
		Mockito.when(fc.getExternalContext()).thenReturn(ec);
		
		// Determine returning of getRequestParameterMap()
		pm = new HashMap<String, String>();
		Mockito.when(ec.getRequestParameterMap()).thenReturn(pm);
		
		// Mock the connection
		PowerMockito.mockStatic(Connection.class);
		conn = mock(Connection.class);
		Mockito.when(Connection.create()).thenReturn(conn);
		
		// Mock CourseDAO
		PowerMockito.mockStatic(UserDAO.class);
		
		readOnly = false;
		salutation = "mr";
		role = "admin";
		
		// Set initial user
		Address address = new Address();
		address.setCity("L.A.");
		address.setCountry("USA");
		address.setHouseNumber(10);
		address.setId(2);
		address.setStreet("Abbey Road");
		address.setZipCode(9723930);
		
		user = new User();
		user.setUserId(10002);
		user.setAccountBalance(0);
		user.setAddress(address);

		SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
			date = dateformat.parse("4.8.1965");
		} catch (ParseException e) {
			LogHandler.getInstance().error("Error ocurred in setup() in class UserProfileBeanTest");
		}
		
		user.setDateOfBirth(date);
		user.setEmail("strat-fan@web.de");
		user.setUsername("Slash");
		user.setFirstname("Saul");
		user.setLastname("Hudson");
		user.setSalutation(Salutation.MR);
		user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
		user.setUserStatus(UserStatus.REGISTERED);
		
		// Set user data which should be updated
		Address checkAddress = new Address();
		checkAddress.setCity("L.A.");
		checkAddress.setCountry("USA");
		checkAddress.setHouseNumber(10);
		checkAddress.setId(2);
		checkAddress.setStreet("Abbey Road");
		checkAddress.setZipCode(9723930);
		
		checkUser = new User();
		checkUser.setUserId(10002);
		checkUser.setAccountBalance(0);
		checkUser.setAddress(checkAddress);

		SimpleDateFormat checkDateformat = new SimpleDateFormat("dd.MM.yyyy");
		Date checkDate = null;
		try {
			checkDate = checkDateformat.parse("4.8.1965");
		} catch (ParseException e) {
			LogHandler.getInstance().error("Error ocurred in setup() in class UserProfileBeanTest");
		}
		
		checkUser.setDateOfBirth(checkDate);
		checkUser.setEmail("strat-fan@web.de");
		checkUser.setUsername("Slash");
		checkUser.setFirstname("Saul");
		checkUser.setLastname("Hudson");
		checkUser.setSalutation(Salutation.MR);
		checkUser.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
		checkUser.setUserStatus(UserStatus.REGISTERED);
		
		bean = new UserProfileBean();
	}
	
	@Test
	public void testSaveSettings() {
		pm.put("userID", "10002");
		
		// Determine the return value of getUser
		Mockito.when(UserDAO.getUser(conn, 10002)).thenReturn(user);
		
		bean.init();
		
		// Determine the return value of nickTaken
		Mockito.when(UserDAO.nickTaken(conn, "blacky")).thenReturn(true);
		
		// Determine the return value of emailExists
		Mockito.when(UserDAO.emailExists(conn, user.getEmail())).thenReturn(true);
		
		// Set nickname to "blacky", which is already in use by another user
		user.setUsername("blacky");
		bean.setSalutation("mr");
		bean.setRole("admin");
		bean.setUser(user);
		
		// Determine the return value of getUser
		Mockito.when(UserDAO.getUser(conn, 10002)).thenReturn(checkUser);
		
		// At this point the user data is not updated, because the nickname is already taken
		bean.saveSettings();
		
		// Set new user nickname
		user.setUsername("Slash90");
		
		// Determine the return value of nickTaken
		Mockito.when(UserDAO.nickTaken(conn, user.getUsername())).thenReturn(true);
		
		// Determine the return value of emailExists
		Mockito.when(UserDAO.emailExists(conn, user.getEmail())).thenReturn(true);
		
		bean.setSalutation("mr");
		bean.setRole("admin");
		bean.setUser(user);
		
		// Determine the return value of getUser
		Mockito.when(UserDAO.getUser(conn, 10002)).thenReturn(checkUser);
		
		// Determine the return value of nickTaken
		Mockito.when(UserDAO.nickTaken(conn, user.getUsername())).thenReturn(false);
		
		// Determine the return value of emailExists
		Mockito.when(UserDAO.emailExists(conn, user.getEmail())).thenReturn(true);
		
		PowerMockito.verifyStatic();
		UserDAO.updateUser(conn, user, null, null);
		
		bean.saveSettings();
		
	}

}
