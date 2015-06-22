package de.ofCourse.action;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.CourseUnitDAO;
import de.ofCourse.Database.dao.CycleDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * 
 * @author Patrick Cretu
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, CourseDAO.class,
	FacesContext.class, PaginationData.class })
public class SearchCourseBeanTest {
	
	// RequestparameterMap
    private Map<String, String> pm;
	
	private SearchCourseBean bean;
	
	private PaginationData pagination;
	
	private String displayPeriod;
	
	private String searchParam;
	
	private String searchString;
	
	private List<Course> searchResult;
	
	//result;
	
	private boolean pagingSearchTerm;
	
	private boolean renderTable;
	
	private boolean columnSort;
	
	private String orderPeriod;
	
	private String orderSearchParam;
	
	private String orderSearchString;
	
	private Connection conn;
	
	@Before
    public void setup() {
		/*PowerMockito.mockStatic(FacesContext.class);
		FacesContext fc = mock(FacesContext.class);
		Mockito.when(FacesContext.getCurrentInstance()).thenReturn(fc);

		// Mock the ExternalContext
		ExternalContext ec = mock(ExternalContext.class);
		Mockito.when(fc.getExternalContext()).thenReturn(ec);

		// Determine returning of getRequestParameterMap()
		pm = new HashMap<String, String>();
		Mockito.when(ec.getRequestParameterMap()).thenReturn(pm);*/

		// Mock the connection
		PowerMockito.mockStatic(Connection.class);
		conn = mock(Connection.class);
		Mockito.when(Connection.create()).thenReturn(conn);
		
		// Mock CourseDAO
		PowerMockito.mockStatic(CourseDAO.class);
		
		pagination = new PaginationData();
    	pagination.setElementsPerPage(10);
    	displayPeriod = "total";
    	searchParam = "title";
    	
    	columnSort = false;
		orderPeriod = displayPeriod;
		orderSearchParam = searchParam;
		orderSearchString = searchString;
	}

	@Test
	public void testSearchByTitle() {
		/*
		// Initializes the session
		pm.put("searchParam", "title");
		pm.put("searchString", "yoga");*/
		
		searchParam = "title";
		searchString = "yoga";
				
		pagination.setSortColumn("title");
		pagination.setSortAsc(true);
		pagination.setCurrentPageNumber(0);
		
		// Determine the return value of getNumberOfCourses
		Mockito.when(CourseDAO.getNumberOfCourses(conn, searchParam, searchString)).thenReturn(1);
		
		pagination.refreshNumberOfPages(1);
		
		//Create course dates
		Date startDate = new Date();
		startDate.setDate(2015-06-15);
		Date endDate = new Date();
		endDate.setDate(2016-06-15);
		
		// Create course
		Course course = new Course();
		course.setCourseID(10051);
		course.setTitle("Yoga234");
		course.setMaxUsers(35);
		course.setStartdate(startDate);
		course.setEnddate(endDate);
		
		//Add course to result list
		List<Course> searchResult = new ArrayList<Course>();
		searchResult.add(course);
		
		// Determine the return value of getCourses
		Mockito.when(CourseDAO.getCourses(conn, pagination, searchParam, searchString)).thenReturn(searchResult);
		
		bean.search();
	}
	
	/*@Test
	public void testSearchByDate() {
		// Initializes the session
		pm.put("searchParam", "date");
		pm.put("searchString", "07.12.2016");
				
		pagination.setSortColumn("date");
		pagination.setSortAsc(true);
		pagination.setCurrentPageNumber(0);
		
		// Determine the return value of getNumberOfCourses
		Mockito.when(CourseDAO.getNumberOfCourses(conn, "date", "07.12.2016")).thenReturn(1);
		
		pagination.refreshNumberOfPages(1);
		
		//Create course dates
		Date startDate = new Date();
		startDate.setDate(2015-4-15);
		Date endDate = new Date();
		endDate.setDate(2016-12-8);
		
		// Create course
		Course course = new Course();
		course.setCourseID(10019);
		course.setTitle("BildTEst");
		course.setMaxUsers(101);
		course.setStartdate(startDate);
		course.setEnddate(endDate);
		
		//Add course to result list
		List<Course> searchResult = new ArrayList<Course>();
		searchResult.add(course);
		
		// Determine the return value of getCourses
		Mockito.when(CourseDAO.getCourses(conn, pagination, "date", "07.12.2016")).thenReturn(searchResult);
	}
	
	@Test
	public void testDisplayCoursesInPeriod() {
		
	}*/

}
