/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Provides functionality for administrators to create and delete courses and to
 * manage the leaders of courses, so to say, to add and to remove leaders from
 * courses.
 * 
 * <p>
 * Only the administrator is able to use this functionalities.<br>
 * Especially only he has the rights to manage the leaders of courses. In order
 * to do so he has the possibility to add a new leader to a course by entering
 * the data of the leader.<br>
 * For deleting a leader from a course the administrator has to selected the
 * leader to delete. He has the possibility to remove more than one leader from
 * a course at once.
 * 
 * @author Katharina Hölzl
 *
 */
@ManagedBean
@RequestScoped
public class CourseManagementBean {

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * Stores the entered or displayed data of the course.
     */
    private Course course;
    
    /**
     * Stores the ID of the course leader.
     */
    private Integer courseLeaderID;
    
    @PostConstruct
    private void init() {
        this.course = new Course();
        this.courseLeaderID = null;
    }

    /**
     * Creates a new course with the entered data and returns the courseDetails
     * page.<br>
     * That means that the method creates a new database entry for the course
     * and stores it in the database.
     * 
     * @return link to the courseDetail page
     */
    public String createCourse() {
        
        int createdCourseID = 0;
        
        this.transaction = Connection.create();
        transaction.start();
        try {
            // Überprüfen, ob die eingegebene E-Mail-Adresse im System
            // bereits existiert.
            
            createdCourseID = CourseDAO.createCourse(this.transaction, this.course);
            
            if (createdCourseID < 0) {

                // Fehlermeldung in den FacesContext werfen, wenn die Mail
                // schon existiert.
                FacesMessageCreator.createFacesMessage(null,
                        "Beim Erstellen des Kurses trat ein Fehler auf!");

                this.transaction.rollback();
                return "/facelets/user/systemAdministrator/createCourse.xhtml?faces-redirect=false";
            } else {

                // Gibt es die angegebene E-Mail-Adresse noch nicht,
                // erstelle einen
                // neuen Benutzer.
                
                // Erfolgsmeldung in den FacesContext werfen.
                FacesMessageCreator.createFacesMessage(null, "Kurs wurde erfolgreich angelegt!");
                
                this.transaction.commit();
                return "/facelets/open/courses/courseDetail.xhtml?id=" + createdCourseID;
            }
        } catch (InvalidDBTransferException e) {
            this.transaction.rollback();
        }
        return "/facelets/user/systemAdministrator/createCourse.xhtml?faces-redirect=false";
    }

    /**
     * Returns the value of the attribute <code>course</code>.
     * 
     * @return the course
     */
    public Course getCourse() {
	return course;
    }

    /**
     * Sets the value of the attribute <code>course</code>.
     * 
     * @param course
     *            the new value of the attribute course
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    

    /**
     * Returns the ManagedProperty <code>SessionUser</code>.
     * 
     * @return the session of the user
     */
    public SessionUserBean getSessionUser() {
	return sessionUser;
    }

    /**
     * Sets the ManagedProperty <code>SessionUser</code>.
     * 
     * @param userSession
     *            session of the user
     */
    public void setSessionUser(SessionUserBean userSession) {
        this.sessionUser = userSession;
    }

    /**
     * Returns the value of the attribute <code>courseLeaderID</code>.
     * 
     * @return the id of the course leader
     */
    public int getCourseLeaderID() {
        return courseLeaderID;
    }

    /**
     * Sets the value of the attribute <code>courseLeaderID</code>.
     * 
     * @param courseLeaderID
     *                  id of the course leader
     */
    public void setCourseLeaderID(int courseLeaderID) {
        this.courseLeaderID = courseLeaderID;
    }
}
