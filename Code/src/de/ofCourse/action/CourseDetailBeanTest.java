/**
 * 
 */
package de.ofCourse.action;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.CourseUnitDAO;
import de.ofCourse.databaseDAO.CycleDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * @author Sebastian Schwarz
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, UserDAO.class,
    CourseUnitDAO.class, CourseDAO.class, CycleDAO.class,
    FacesContext.class, PaginationData.class })
public class CourseDetailBeanTest {
    
    private CourseDetailBean bean, courseDetailBean; 
    private Connection connection;
    User userToSignUp;
    private Course courseWhereToSignUp, testCourse;
    private SessionUserBean sessionUser;
    
    List<CourseUnit> resultList = new ArrayList<CourseUnit>();
    
    private ResultSet resultSet = null;

    private int SAMPLE_ID = 10050;
    
    
    @Before
    public void setup(){
        
        PowerMockito.mockStatic(FacesContext.class);
        FacesContext fc = mock(FacesContext.class);
        Mockito.when(FacesContext.getCurrentInstance()).thenReturn(fc);

        // Mock the ExternalContext
        ExternalContext ec = mock(ExternalContext.class);
        Mockito.when(fc.getExternalContext()).thenReturn(ec);
        
     // Mock the connection
        PowerMockito.mockStatic(Connection.class);
        connection = mock(Connection.class);
        Mockito.when(Connection.create()).thenReturn(connection);

     // Mock UserDAO
        PowerMockito.mockStatic(UserDAO.class);

        // Mock CourseDAO
        PowerMockito.mockStatic(CourseDAO.class);
        
     // Mock CourseUnitDAO
        PowerMockito.mockStatic(CourseUnitDAO.class);
        
        userToSignUp = new User();
        userToSignUp.setUserId(1);
        userToSignUp.setFirstname("Sebastian");
        userToSignUp.setLastname("Schwarz");
        userToSignUp.setDateOfBirth(new Date(1990, 2, 8));
        userToSignUp.setEmail("sebastian@nrschwarz.de");
        userToSignUp.setUsername("blacky");
        
        courseWhereToSignUp = new Course();
        courseWhereToSignUp.setCourseID(10000);
        courseWhereToSignUp.setMaxUsers(10);
        
        sessionUser = new SessionUserBean();
        sessionUser.setUserID(1);
        
      
               
        bean = new CourseDetailBean();
        bean.setSessionUser(sessionUser);
        bean.setCourseID(10000);
        
        Mockito.when(UserDAO.getUser(connection, userToSignUp.getUserID())).thenReturn(userToSignUp);
        
        Mockito.when(UserDAO.getUser(connection, userToSignUp.getUsername())).thenReturn(userToSignUp);
        
        Mockito.when(CourseDAO.getNumberOfParticipants(connection, courseWhereToSignUp.getCourseID())).thenReturn(0);
        
        Mockito.when(CourseDAO.getCourse(connection, courseWhereToSignUp.getCourseID())).thenReturn(courseWhereToSignUp);
        
        Mockito.when(CourseUnitDAO.getCourseUnitsOf(connection, userToSignUp.getUserID())).thenReturn(resultList);
        
        //Mockito.when(CourseUnitDAO.getResult(resultSet)).thenReturn(resultList);

        //ab hier Code von Ricky Strohmeier
        testCourse = new Course();
        testCourse.setCourseID(SAMPLE_ID);
        testCourse.setTitle("Unmodified TestCourse");
        testCourse.setDescription("This is the unmodifierd TestCourse");

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date endDate, startDate;

        try {
            endDate = df.parse("10/12/2015");
            testCourse.setEnddate(endDate);
            
            startDate = df.parse("11/12/2015"); 
            testCourse.setStartdate(startDate);            
        } catch(ParseException e) {
            //ignored
        }

        testCourse.setMaxUsers(100);
        courseDetailBean = new CourseDetailBean();
        courseDetailBean.setCourse(testCourse);
    }

    /**
     * This method tests if the editation of a course is working properly.
     * 
     * @author Ricky Strohmeier
     */
    @Test
    public void testCourseEditation() {
        Course editedCourse = new Course();
        editedCourse.setCourseID(courseDetailBean.getCourse().getCourseID());

        assertEquals(false, courseDetailBean.getEditMode());
        courseDetailBean.enableEditMode();
        assertEquals(true, courseDetailBean.getEditMode());

        courseDetailBean.saveCourse();
        assertEquals(false, courseDetailBean.getEditMode());
    }

    /**
     * Test method for {@link de.ofCourse.action.CourseDetailBean#signUpForCourse()}.
     */
    @Test
    public void testSignUpForCourse() {
        bean.signUpForCourse();
        
        PowerMockito.verifyStatic();
        CourseDAO.getNumberOfParticipants(connection, courseWhereToSignUp.getCourseID());
        
        PowerMockito.verifyStatic();
        CourseDAO.getCourse(connection, courseWhereToSignUp.getCourseID() );
        
        
    }
    
    
    /**
     * 
     */
    @Test
    public void testSignOffFromCourse(){
        bean.signOffFromCourse();
        
        
        
        PowerMockito.verifyStatic();
        Mockito.when(CourseUnitDAO.getCourseUnitsOf(connection, userToSignUp.getUserID())).thenReturn(resultList);
        
        PowerMockito.verifyStatic();
        Mockito.when(UserDAO.getUser(connection, userToSignUp.getUserID())).thenReturn(userToSignUp);
    }
    

}
