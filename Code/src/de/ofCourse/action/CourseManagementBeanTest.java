package de.ofCourse.action;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.*;

import de.ofCourse.model.Course;

public class CourseManagementBeanTest extends TestCase{
    
    private CourseManagementBean courseManagementBean;
    
    @Before
    public void setUp() {
      courseManagementBean = new CourseManagementBean(); 
    } 
    
    @Test
    public void test() {
        
        courseManagementBean.activateTesting();
        
        Course course = new Course();
        course.setCourseID(10);
        course.setDescription("Testkurs Beschreibung");
        course.setEnddate(new Date(1234567890));
        course.setStartdate(new Date(123456789));
        course.setMaxUsers(10);
        course.setTitle("Testkurs");        
        
        courseManagementBean.setCourse(course);
        
        assertEquals(courseManagementBean.getCourse(), course);

        assertEquals(courseManagementBean.createCourse(), "/facelets/open/courses/courseDetail.xhtml?faces-redirect=true&id=0");
        
    }

}
