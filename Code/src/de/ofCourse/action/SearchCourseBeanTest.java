package de.ofCourse.action;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * 
 * JUnit test for the class SearchCourseBean.
 * 
 * @author Patrick Cretu
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, CourseDAO.class,
	FacesContext.class, PaginationData.class })
public class SearchCourseBeanTest {
	
	/**
	 * The backing bean attribute
	 */
	private SearchCourseBean bean;
	
	/**
	 * The bean's pagination data
	 */
	private PaginationData pagination;
	
	/**
	 * The attribute presenting the user's selected search parameter
	 */
	private String searchParam;
	
	/**
	 * The attribute presenting the user's selected search input
	 */
	private String searchString;
	
	/**
	 * Connection to access the database
	 */
	private Connection conn;
	
	@Before
    public void setup() {
		// Mock the connection
		PowerMockito.mockStatic(Connection.class);
		conn = mock(Connection.class);
		Mockito.when(Connection.create()).thenReturn(conn);
		
		// Mock CourseDAO
		PowerMockito.mockStatic(CourseDAO.class);
		
		pagination = new PaginationData();
    	pagination.setElementsPerPage(10);
    	searchParam = "title";
    	
    	bean = new SearchCourseBean();
		bean.init();
	}

	/**
	 * Tests the backing bean method searchByTitle()
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSearchByTitle() {		
		bean.setSearchParam("titel");
		bean.setSearchString("yoga");
				
		pagination.setSortColumn(SortColumn.TITLE);
		pagination.setSortDirection(SortDirection.ASC);
		pagination.setCurrentPageNumber(0);
		
		// Determine the return value of getNumberOfCourses
		Mockito.when(CourseDAO.getNumberOfCourses(conn, searchParam,
				searchString)).thenReturn(1);
		
		pagination.refreshNumberOfPages(1);
		
		bean.setPagination(pagination);
		
		//Create course dates
		Date startDate = new Date();
		startDate.setDate(2015-6-15);
		Date endDate = new Date();
		endDate.setDate(2016-6-15);
		
		// Create course
		Course course = new Course();
		course.setCourseID(10004);
		course.setTitle("Yoga234");
		course.setMaxUsers(35);
		course.setStartdate(startDate);
		course.setEnddate(endDate);
		
		//Add course to result list
		List<Course> searchResult = new ArrayList<Course>();
		searchResult.add(course);
		
		// Determine the return value of getCourses
		Mockito.when(CourseDAO.getCourses(conn, pagination, searchParam,
				searchString)).thenReturn(searchResult);
		
		bean.search();
	}
	
	/**
	 * Tests the backing bean method searchByDate()
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSearchByDate() {
		bean.setSearchParam("start_date");
		bean.setSearchString("6.7.2015");
				
		pagination.setSortColumn(SortColumn.START_DATE);
		pagination.setSortDirection(SortDirection.ASC);
		pagination.setCurrentPageNumber(0);
		
		// Determine the return value of getNumberOfCourses
		Mockito.when(CourseDAO.getNumberOfCourses(conn, searchParam,
				searchString)).thenReturn(1);
		
		pagination.refreshNumberOfPages(1);
		
		bean.setPagination(pagination);
		
		//Create course dates
		Date startDate = new Date();
		startDate.setDate(2015-4-15);
		Date endDate = new Date();
		endDate.setDate(2016-12-8);
		
		// Create course
		Course course = new Course();
		course.setCourseID(10005);
		course.setTitle("BildTest");
		course.setMaxUsers(101);
		course.setStartdate(startDate);
		course.setEnddate(endDate);
		
		//Add course to result list
		List<Course> searchResult = new ArrayList<Course>();
		searchResult.add(course);
		
		// Determine the return value of getCourses
		Mockito.when(CourseDAO.getCourses(conn, pagination, searchParam,
				searchString)).thenReturn(searchResult);
		
		bean.search();
	}

}
