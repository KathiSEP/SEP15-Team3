package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.GregorianCalendar;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.CourseUnitDAO;
import de.ofCourse.databaseDAO.CycleDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Cycle;
import de.ofCourse.model.Language;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.Period;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.LanguageManager;


/**
 * JUnit test for the class CourseUnitManagementBean.
 * 
 * @author Tobias Fuchs
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class,
                  Connection.class, 
                  UserDAO.class,
	          CourseUnitDAO.class, 
	          CourseDAO.class, 
	          CycleDAO.class,
	          FacesContext.class,
	          PaginationData.class, 
	          LanguageManager.class,
	          FacesMessageCreator.class })
public class CourseUnitManagementBeanTest {

    //bean attribute
    private CourseUnitManagementBean bean;

    // RequestparameterMap
    private Map<String, String> pm;

    // participant user
    private User part;
    
    // course admin
    private User admin;
    
    //Course units
    private CourseUnit unit;
    private CourseUnit unit2;
    private CourseUnit unit3;
    
    //corresponding course
    private Course course;
    
    //SessionUserBean to define the language
    private SessionUserBean sessionUser;

    //Cycle of regular course unit
    @SuppressWarnings("unused")
    private String enteredTurnus;
    private Cycle cycle;


    //Participants lists of the course units
    private List<User> participants;
    private List<User> participants2;
    private List<User> participants3;

    private LanguageManager myLang;

    // Connection for database access
    private Connection conn;

    private PaginationData pagination;
    
    @SuppressWarnings("deprecation")
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

	// Mock CourseUnitDAO
	PowerMockito.mockStatic(CourseUnitDAO.class);

	// Mock CourseUnitDAO
	PowerMockito.mockStatic(UserDAO.class);

	// Mock CourseDAO
	PowerMockito.mockStatic(CourseDAO.class);

	// Mock CycleaDAO
	PowerMockito.mockStatic(CycleDAO.class);
	
	//Mock LanguageManagerCreator
	PowerMockito.mockStatic(LanguageManager.class);
	myLang = mock(LanguageManager.class);
	
	// Mock FacesMessageCreator
	PowerMockito.mockStatic(FacesMessageCreator.class);
	

	// Create courseUnit
	CourseUnit cu1 = new CourseUnit();
	cu1.setStartime(new GregorianCalendar(2015, 6, 1).getTime());
	cu1.setEndtime(new GregorianCalendar(2015, 6, 2).getTime());

	// Initialization work
	participants = new ArrayList<User>();

	part = new User();
	part.setUserId(1);
	part.setFirstname("Tobias");
	part.setLastname("Fuchs");
	part.setDateOfBirth(new Date(1993, 5, 29));
	part.setEmail("fuchs249@live.de");
	part.setUsername("Tobi249");
	part.setAccountBalance(20);

	cycle = new Cycle();
	cycle.setCycleID(0);
	cycle.setCourseID(1);
	cycle.setNumberOfUnits(3);
	cycle.setTurnus(Period.DAYS);

	unit = new CourseUnit();
	unit.setCourseUnitID(1);
	unit.setPrice(2);
	unit.setCourseID(1);
	unit.setCycle(cycle);

	unit2 = new CourseUnit();
	unit2.setCourseUnitID(2);
	unit2.setPrice(2);
	unit2.setCourseID(1);
	unit2.setCycle(cycle);

	unit3 = new CourseUnit();
	unit3.setCourseUnitID(3);
	unit3.setPrice(2);
	unit3.setCourseID(1);
	unit3.setCycle(cycle);

	pagination = new PaginationData();
	pagination.setCurrentPageNumber(0);
	pagination.setElementsPerPage(10);
	pagination.setNumberOfPages(1);
	pagination.setSortDirection(SortDirection.ASC);
	pagination.setSortColumn(SortColumn.TITLE);

	course = new Course();
	course.setStartdate(new Date(2015, 1, 1));
	course.setEnddate(new Date(2015, 12, 12));

	participants = new ArrayList<User>();
	participants.add(part);

	participants2 = new ArrayList<User>();
	participants2.add(part);

	participants3 = new ArrayList<User>();
	participants3.add(part);
	
	admin = new User();
	admin.setUserID(2);
	
	sessionUser = new SessionUserBean();
	sessionUser.setLanguage(Language.DE);
	
	List<Integer> idsToDelete = new ArrayList<Integer>();
	idsToDelete.add(1);
	idsToDelete.add(2);
	idsToDelete.add(3);
	
	// Determine the return values of called static methods
	Mockito.when(CourseUnitDAO.getCourseUnit(conn, 1)).thenReturn(cu1);
	
	Mockito.when(CourseUnitDAO.getParticipiantsOfCourseUnit(
		conn,
		pagination,
		1,
		false)).thenReturn(participants);

	Mockito.when(CourseDAO.getCourse(conn, 1)).thenReturn(course);

	Mockito.when(CycleDAO.createCycle(conn, 1, cycle)).thenReturn(1);

	Mockito.when(CourseUnitDAO.getIdsCourseUnitsOfCycle(conn, 1))
		.thenReturn(idsToDelete);

	Mockito.when(CourseUnitDAO.getParticipiantsOfCourseUnit(
		conn,
		pagination,
		1,	
		true)).thenReturn(participants);
	
	Mockito.when(CourseUnitDAO.getParticipiantsOfCourseUnit(
		conn,
		pagination,
		2,
		true)).thenReturn(participants2);
	
	Mockito.when(CourseUnitDAO.getParticipiantsOfCourseUnit(
		conn,
		pagination,
		3,
		true)).thenReturn(participants2);

	Mockito.when(CourseUnitDAO.getPriceOfUnit(conn, 1))
		.thenReturn((float) 2.0);
	Mockito.when(CourseUnitDAO.getPriceOfUnit(conn, 2))
		.thenReturn((float) 2.0);
	Mockito.when(CourseUnitDAO.getPriceOfUnit(conn, 3))
		.thenReturn((float) 2.0);

	Mockito.when(UserDAO.userIsParticipantInCourseUnit(conn, 1, 1))
		.thenReturn(true);
	Mockito.when(UserDAO.userIsParticipantInCourseUnit(conn, 1, 2))
		.thenReturn(true);
	Mockito.when(UserDAO.userIsParticipantInCourseUnit(conn, 1, 3))
		.thenReturn(true);
	
	Mockito.when(CourseDAO.getLeaders(conn, 1)).thenReturn(Arrays.asList(admin));
	
	Mockito.when(LanguageManager.getInstance()).thenReturn(myLang);

	// Create the course unit management
	bean = new CourseUnitManagementBean();
    }

    
    /**
     * Tests the addUserToCourseUnit() - method
     */
    @Test
    public void testAddUserToCourseUnit() {
	// Initializes the session
	pm.put("editMode", "true");
	pm.put("courseID", "1");
	pm.put("courseUnitID", "1");

	// Initializes the Bean
	bean.init();

	//Does some further initialization
	bean.setPagination(pagination);
	bean.setCourseUnit(unit);
	bean.setUserToAdd(part);
	assertEquals(pagination, bean.getPagination());

	// Adds user part to course unit unit
	bean.addUserToCourseUnit();

	// Checks whether the methods were executed correctly
	PowerMockito.verifyStatic();
	UserDAO.updateAccountBalance(conn, 1, 18);

	PowerMockito.verifyStatic();
	CourseUnitDAO.addUserToCourseUnit(conn, part.getUserID(),
		unit.getCourseUnitID());

	PowerMockito.verifyStatic();
	CourseUnitDAO.getParticipiantsOfCourseUnit(conn, pagination, 1, false);
    }

    /**
     * Tests the deleteCourseUnit - method
     */
    @Test
    public void testDeleteCourseUnit() {

	// Initializes the session
	pm.put("editMode", "true");
	pm.put("courseID", "1");
	pm.put("courseUnitID", "1");

	//Initialization work
	bean.init();
	bean.setPagination(pagination);
	bean.setCourseUnit(unit);
	bean.setCompleteCycle(true);
	bean.setSelectedToInform(0);

	// Executes the deleteCourseUnit() method
	String url = bean.deleteCourseUnit();

	// Checks whether the methods were executed
	PowerMockito.verifyStatic();
	CourseUnitDAO.getIdsCourseUnitsOfCycle(conn, 1);

	// Fetches the participants of unit
	PowerMockito.verifyStatic(times(1));
	CourseUnitDAO.getParticipiantsOfCourseUnit(conn, pagination, 1, true);

	// Fetches the participants of unit1
	PowerMockito.verifyStatic(times(1));
	CourseUnitDAO.getParticipiantsOfCourseUnit(conn, pagination, 2, true);

	// Fetches the participants of unit2
	PowerMockito.verifyStatic(times(1));
	CourseUnitDAO.getParticipiantsOfCourseUnit(conn, pagination, 3, true);

	// Removes user part from course unit unit
	// (In reality only invoked once but in test the database mock returns
	// always true )
	PowerMockito.verifyStatic(times(3));
	CourseUnitDAO.removeUserFromCourseUnit(conn, 1, 1);

	// Removes user part from course unit unit1
	// (In reality only invoked once but in test the database mock returns
	// always true )
	PowerMockito.verifyStatic(times(3));
	CourseUnitDAO.removeUserFromCourseUnit(conn, 1, 2);

	// Removes user part from course unit unit2
	// (In reality only invoked once but in test the database mock returns
	// always true )
	PowerMockito.verifyStatic(times(3));
	CourseUnitDAO.removeUserFromCourseUnit(conn, 1, 3);
	// Updates the account balance of the user with the sum of all prices of
	// all signed off units
	// (In reality only invoked once but in test the database mock returns
	// always true )
	PowerMockito.verifyStatic(times(3));
	UserDAO.updateAccountBalance(conn, 1, 26);

	// Delete the units
	PowerMockito.verifyStatic();
	CourseUnitDAO.deleteCourseUnit(conn, 1);
	
	PowerMockito.verifyStatic();
	CourseUnitDAO.deleteCourseUnit(conn, 2);

	PowerMockito.verifyStatic();
	CourseUnitDAO.deleteCourseUnit(conn, 3);
	
	//Deletes the corresponding cycle
	PowerMockito.verifyStatic();
	CycleDAO.deleteCycle(conn, 0);

	//Checks whether you are redirected to course details page
	assertEquals(url, "/facelets/open/courses/courseDetail.xhtml");
    }

    /**
     * Tests the createCourseUnit() - method
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateCourseUnit() {
	// Initializes the session
	pm.put("editMode", "false");
	pm.put("courseID", "1");
	pm.put("courseUnitID", "1");

	//Initialization work
	bean.init();
	bean.setCourseUnit(unit);
	bean.setEnteredTurnus("WEEKS");
	bean.getCourseUnit().setCourseAdmin(admin);

	// Regular unit
	bean.setRegularCourseUnit(true);

	// Entered date for unit is in rage of corresponding course
	bean.setDate(new Date(2015, 2, 2));
	bean.setStart(new Date(2015, 2, 2, 12, 0));
	bean.setEnd(new Date(2015, 2, 2, 14, 0));

	// Checks whether you are redirected to course detail page
	String url = bean.createCourseUnit();
	assertEquals(url, "/facelets/open/courses/courseDetail.xhtml");

	// Check whether the entered dates are in course range
	// Check whether the automatically created cycle units are in course
	// range
	PowerMockito.verifyStatic(times(4));
	CourseDAO.getCourse(conn, 1);

	PowerMockito.verifyStatic();
	CycleDAO.createCycle(conn, 1, cycle);

	// The number of units in the cycle was set to 3
	PowerMockito.verifyStatic(times(3));
	CourseUnitDAO.createCourseUnit(conn, unit, unit.getCourseID(), true);

	// Entered date for unit is not in rage of corresponding course
	bean.setDate(new Date(2016, 2, 2));
	bean.setStart(new Date(2016, 2, 2, 12, 0));
	bean.setEnd(new Date(2016, 2, 2, 14, 0));
        bean.setSessionUser(sessionUser);
	
        // Check whether you stay on the same page in case of the entered dates
        // are not in range of the course
	String url2 = bean.createCourseUnit();
	assertEquals(url2, "x");
    }
}
