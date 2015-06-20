package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.PaginationData;
import de.ofCourse.system.Connection;

public class SearchCourseBeanTest {
	
	// RequestparameterMap
    private Map<String, String> pm;
	
	private SearchCourseBean bean;
	
	private PaginationData pagination;
	
	private Connection conn;
	
	@SuppressWarnings("deprecation")
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
    	pagination.setSortAsc(true);
	}

	@Test
	public void testSearch() {
		// Initializes the session
		pm.put("searchParam", "title");
		pm.put("searchString", "test");
		
		
	}

}
