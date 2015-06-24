package de.ofCourse.action;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.Language;
import de.ofCourse.system.Connection;
import de.ofCourse.utilities.LanguageManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Connection.class, CourseDAO.class, FacesMessage.class, 
    FacesMessageCreator.class, InvalidDBTransferException.class, 
    FacesContext.class, LanguageManager.class})
public class CourseManagementBeanTest extends TestCase{
    
    private CourseManagementBean courseManagementBean;
    
    private SessionUserBean sessionUser;
    
    private LanguageManager languageManager;
    
    @Mock
    FacesContext facesContext;
    
    @Mock
    ExternalContext externalContext;
    
    ArgumentCaptor<String> clientIdCaptor;
    ArgumentCaptor<FacesMessage> facesMessageCaptor;
    
    private Map<String, String> requestParameterMap;
    private Connection connection;
    private FacesMessage captured;
    
    private Course correctCourse;
    private Course wrongCourse;
    private int creatingCourseFailed;
    private int creatingCourseSucceeded;
    
    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        PowerMockito.mockStatic(FacesContext.class);
        
        Mockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        
        // Nur der Vollständigkeit halber
        requestParameterMap = new HashMap<String, String>();
        Mockito.when(externalContext.getRequestParameterMap()).thenReturn(requestParameterMap);
        
        PowerMockito.mockStatic(Connection.class);
        connection = mock(Connection.class);          
        Mockito.when(Connection.create()).thenReturn(connection);
        
        sessionUser = new SessionUserBean();
        sessionUser.setLanguage(Language.DE);
                
        //Mock LanguageManagerCreator
        PowerMockito.mockStatic(LanguageManager.class);
        languageManager = mock(LanguageManager.class);
 
        Mockito.when(LanguageManager.getInstance()).thenReturn(languageManager);
        
        Mockito.when(LanguageManager.getInstance().
                getProperty("courseManagementBean.facesMessage.CourseMistake", Language.DE)).
                thenReturn("Beim Erstellen des Kurses trat ein Fehler auf!");
        
        Mockito.when(LanguageManager.getInstance().
                getProperty("courseManagementBean.facesMessage.CourseSuccessful", Language.DE)).
                thenReturn("Kurs wurde erfolgreich angelegt!");
                
        PowerMockito.mockStatic(CourseDAO.class);
        
        wrongCourse = new Course();
        wrongCourse.setTitle("Falscher Kurs");
        wrongCourse.setDescription("Das ist ein falscher Kurs.");
        wrongCourse.setMaxUsers(3);
        wrongCourse.setStartdate(new Date(2014, 10, 10));
        wrongCourse.setEnddate(new Date(2016, 10, 10));
        
        correctCourse = new Course();
        correctCourse.setTitle("Richtiger Kurs");
        correctCourse.setDescription("Das ist ein richtiger Kurs.");
        correctCourse.setMaxUsers(30);
        correctCourse.setStartdate(new Date(2013, 11, 11));
        correctCourse.setEnddate(new Date(2017, 11, 11));        
        
        creatingCourseFailed = -1;
        Mockito.when(CourseDAO.createCourse(connection, wrongCourse, null)).thenReturn(creatingCourseFailed);
        
        creatingCourseSucceeded = 1;
        Mockito.when(CourseDAO.createCourse(connection, correctCourse, null)).thenReturn(creatingCourseSucceeded);

        Mockito.when(CourseDAO.addLeaderToCourse(eq(connection), anyInt(), anyInt())).thenReturn(true);

        clientIdCaptor = ArgumentCaptor.forClass(String.class);
        facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
    } 
    
    @Test
    public void test() {
        courseManagementBean = new CourseManagementBean();
        
        courseManagementBean.setSessionUser(sessionUser);
        
        courseManagementBean.setCourse(wrongCourse);
        
        courseManagementBean.setCourseLeaderID(12345);
        
        assertEquals(courseManagementBean.createCourse(), "/facelets/user/systemAdministrator/createCourse.xhtml?faces-redirect=false");
        
        // FacesMessage prüfen
        verify(facesContext, times(1)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Beim Erstellen des Kurses trat ein Fehler auf!", captured.getSummary());
        
        courseManagementBean.setCourse(correctCourse);
        
        assertEquals(courseManagementBean.createCourse(), "/facelets/open/courses/courseDetail.xhtml?"
                        + "faces-redirect=true&courseID="
                        + creatingCourseSucceeded);
        
        verify(facesContext, times(2)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Kurs wurde erfolgreich angelegt!", captured.getSummary());
    }

}
