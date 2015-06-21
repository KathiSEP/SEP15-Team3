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
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.system.Connection;

public class SearchCourseBeanTest {
	
	// RequestparameterMap
    private Map<String, String> pm;
	
	private SearchCourseBean bean;
	
	private PaginationData pagination;
	
	private String displayPeriod;
	
	private Connection conn;
	
	@Before
    public void setup() {
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
		PowerMockito.mockStatic(CourseDAO.class);
		
		pagination = new PaginationData();
    	pagination.setElementsPerPage(10);
    	displayPeriod = "total";
	}

	@Test
	public void testSearch() {
		// Initializes the session
		pm.put("searchParam", "title");
		pm.put("searchString", "test");
		
		bean.init();
		
		pagination.setSortColumn("title");
		pagination.setSortAsc(true);
		pagination.setCurrentPageNumber(0);
		
		// Determine the return value of getNumberOfCourses
		Mockito.when(CourseDAO.getNumberOfCourses(conn, "title", "yoga")).thenReturn(1);
		
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
		Mockito.when(CourseDAO.getCourses(conn, pagination, "title", "yoga")).thenReturn(searchResult);
		
		bean.search();
	}

}
