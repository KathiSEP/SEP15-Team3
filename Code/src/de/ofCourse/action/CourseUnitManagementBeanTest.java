package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.GregorianCalendar;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.CourseUnitDAO;
import de.ofCourse.Database.dao.CycleDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Cycle;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, UserDAO.class,
	CourseUnitDAO.class, CourseDAO.class, CycleDAO.class,
	FacesContext.class, PaginationData.class })
public class CourseUnitManagementBeanTest {

    private CourseUnitManagementBean bean;

    // RequestparameterMap
    private Map<String, String> pm;

    private User part;
    private CourseUnit unit;
    private Course course;

    private Cycle cycle;

    private PaginationData pagination;

    private List<User> participants;

    private Connection conn;

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

	// Create courseUnit
	CourseUnit cu1 = new CourseUnit();
	cu1.setStartime(new GregorianCalendar(2015, 6, 1).getTime());
	cu1.setEndtime(new GregorianCalendar(2015, 6, 2).getTime());

	// Determine the return value of getCourseUnit
	Mockito.when(CourseUnitDAO.getCourseUnit(conn, 1)).thenReturn(cu1);

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
	cycle.setCycleID(1);
	cycle.setCourseID(1);
	cycle.setNumberOfUnits(3);
	cycle.setTurnus(7);

	unit = new CourseUnit();
	unit.setCourseUnitID(1);
	unit.setPrice(2);
	unit.setCourseID(1);
	unit.setCycle(cycle);

	pagination = new PaginationData();
	pagination.setCurrentPageNumber(0);
	pagination.setElementsPerPage(10);
	pagination.setNumberOfPages(1);
	pagination.setSortAsc(true);
	pagination.setSortColumn("name");

	course = new Course();
	course.setStartdate(new Date(2015, 1, 1));
	course.setEnddate(new Date(2015, 12, 12));

	ArrayList<User> participants = new ArrayList<User>();
	participants.add(part);

	Mockito.when(CourseUnitDAO.getParticipiantsOfCourseUnit(conn,
		pagination, 
		1,
		false))
		.thenReturn(participants);

	Mockito.when(CourseDAO.getCourse(conn, 1)).thenReturn(course);

	Mockito.when(CycleDAO.createCycle(conn, 1, cycle)).thenReturn(1);



	// Create the course unit management
	bean = new CourseUnitManagementBean();
    }

    @Test
    public void testAddUserToCourseUnit() {

	// Initializes the session
	pm.put("editMode", "true");
	pm.put("courseID", "1");
	pm.put("courseUnitID", "1");

	DataModel<User> dataModelUserList = new ListDataModel<User>();
	dataModelUserList.setWrappedData(participants);

	// Initializes the Bean
	bean.init();
	bean.setParticipants(dataModelUserList);
	bean.setPagination(pagination);
	bean.setCourseUnit(unit);
	bean.setUserToAdd(part);
	assertEquals(pagination, bean.getPagination());

	// Adds user part to course unit unit
	bean.addUserToCourseUnit();

	// Checks whether the methods were executed
	PowerMockito.verifyStatic();
	UserDAO.updateAccountBalance(conn, 1, 18);

	PowerMockito.verifyStatic();
	CourseUnitDAO.addUserToCourseUnit(conn, part.getUserID(),
		unit.getCourseUnitID());

	PowerMockito.verifyStatic();
	CourseUnitDAO.getParticipiantsOfCourseUnit(conn, pagination, 1, false);

    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCreateCourseUnit() {
	// Initializes the session
	pm.put("editMode", "false");
	pm.put("courseID", "1");
	pm.put("courseUnitID", "1");

	bean.init();
	bean.setCourseUnit(unit);

	//Regular unit
	bean.setRegularCourseUnit(true);

	// Entered date for unit is in rage of corresponding course
	bean.setDate(new Date(2015, 2, 2));
	bean.setStart(new Date(2015, 2, 2, 12, 0));
	bean.setEnd(new Date(2015, 2, 2, 14, 0));

	String url = bean.createCourseUnit();
	assertEquals(url, "/facelets/open/courses/courseDetail.xhtml");

	PowerMockito.verifyStatic();
	CourseDAO.getCourse(conn, 1);

	PowerMockito.verifyStatic();
	CycleDAO.createCycle(conn, 1, cycle);
	
	//The number of units in the cycle was set to 3
	PowerMockito.verifyStatic(times(3));
	CourseUnitDAO.createCourseUnit(conn, unit, unit.getCourseID(), true);
	

	// Entered date for unit is not in rage of corresponding course
	bean.setDate(new Date(2016, 2, 2));
	bean.setStart(new Date(2016, 2, 2, 12, 0));
	bean.setEnd(new Date(2016, 2, 2, 14, 0));

	String url2 = bean.createCourseUnit();
	assertEquals(url2, "x");

    }

}
