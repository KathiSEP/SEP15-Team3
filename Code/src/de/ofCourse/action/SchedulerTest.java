package de.ofCourse.action;

import static org.junit.Assert.*;

import javax.faces.context.FacesContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.CourseDAO;
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
public class SchedulerTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
