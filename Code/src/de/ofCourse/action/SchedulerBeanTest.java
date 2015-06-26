package de.ofCourse.action;

import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.CourseUnitDAO;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * 
 * JUnit test for the class SchedulerBean.
 * 
 * @author Patrick Cretu
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, CourseUnitDAO.class })
public class SchedulerBeanTest {
	
	/**
	 * The week's current Monday
	 */
	private Date currentMonday;
	
	/**
	 * The week's current Sunday
	 */
	private Date currentSunday;
	
	/**
	 * Connection to access the database
	 */
	private Connection conn;
	
	/**
	 * The backing bean attribute
	 */
	private SchedulerBean bean;
	
	@Before
    public void setup() {
		// Mock the connection
		PowerMockito.mockStatic(Connection.class);
		conn = mock(Connection.class);
		Mockito.when(Connection.create()).thenReturn(conn);
		
		// Mock CourseDAO
		PowerMockito.mockStatic(CourseUnitDAO.class);
		
		// initialize date (Sunday) from where to begin the computation of the
		// following week
		Date date = Date.valueOf("2015-7-8");
		
		Calendar calMon = Calendar.getInstance();
		calMon.setTime(date);
		calMon.add(Calendar.DATE, +1);
		currentMonday = new Date(calMon.getTime().getTime());
		
		Calendar calSun = Calendar.getInstance();
		calSun.setTime(currentMonday);
		calSun.add(Calendar.DATE, +6);
		currentSunday = new Date(calSun.getTime().getTime());
		
		bean = new SchedulerBean();
	}
	
	/**
	 * Tests the backing bean method displayNextWeek()
	 */
	@Test
	public void testDisplayNextWeek() {
		// Initialize the result list of course units
		List<CourseUnit> weeklyUnits = new ArrayList<CourseUnit>();
		CourseUnit unit = new CourseUnit();
		unit.setCourseUnitID(10005);
		unit.setCourseID(10005);
		unit.setTitle("Bilder");
		unit.setPrice(0);
		unit.setStartime(Date.valueOf("2015-7-10"));
		unit.setEndtime(Date.valueOf("2015-7-10"));
		weeklyUnits.add(unit);
		
		// Determine the return value of getWeeklyCourseUnitsOf
		Mockito.when(CourseUnitDAO.getWeeklyCourseUnitsOf(conn, 10002,
				currentMonday)).thenReturn(weeklyUnits);
		
		SessionUserBean sessionUser = new SessionUserBean();
		sessionUser.setUserID(10002);
		
		bean.setSessionUser(sessionUser);
		bean.setCurrentMonday(currentMonday);
		bean.setCurrentSunday(currentSunday);
		
		bean.displayNextWeek();
	}

}
