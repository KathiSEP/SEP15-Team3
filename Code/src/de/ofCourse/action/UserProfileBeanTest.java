package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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
import de.ofCourse.system.Transaction;

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
		Date date = new Date("4.8.1965");
		user.setDateOfBirth(date);
		user.setEmail("strat-fan@web.de");
		user.setUsername("Slash");
		user.setSalutation(Salutation.MR);
		user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
		user.setUserStatus(UserStatus.REGISTERED);
		
		
		bean = new UserProfileBean();
	}
	
	@Test
	public void testSaveSettings() {
		pm.put("userID", "10002");
		
		
		
	}

}
