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
import de.ofCourse.system.Connection;

public class SearchCourseBeanTest {
	
	// RequestparameterMap
    private Map<String, String> pm;
	
	private SearchCourseBean bean;
	
	private Connection conn;
	
	@SuppressWarnings("deprecation")
    @Before
    public void setup() {
		
	}

	@Test
	public void testSearch() {
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
		
		// Create courseUnit
		CourseUnit cu1 = new CourseUnit();
		cu1.setStartime(new GregorianCalendar(2015, 6, 1).getTime());
		cu1.setEndtime(new GregorianCalendar(2015, 6, 2).getTime());
		
	}

}
