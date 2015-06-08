/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.CourseRegistrationException;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Displays the details of a course and its course units and provides the
 * functionality to edit the data of the course. In addition it manages the sign
 * up and sign off action of courses and course units.
 * <p>
 * 
 * A user can sign up for a course if he's not already signed in.<br>
 * He also can sign up for course units of the course. This is only possible if
 * the user is signed in in the respective course. The payment for a course unit
 * is done automatically by the system, that means that the price of the course
 * unit the user wants to sign in is automatically transfered from the users
 * account. That means that a user only can sign up for a course unit if he has
 * enough money on his account.<br>
 * If the user signs off from a course unit the paid money for the course unit
 * is transfered back to the users account automatically.
 * 
 * This class is Bean and controller of the facelet <code>coursDetails</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@ViewScoped
public class CourseDetailBean implements Pagination {
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the displayed or entered data of the course.
     */
    private Course course;

    /**
     * Stores whether a user registers for course news.
     */
    private boolean registeredForCourseNews;

    /**
     * Stores a list of course units that the user has selected
     */
    private List<CourseUnit> selectedCourseUnits;

    /**
     * Stores a list of leaders of the course
     */
    private List<User> leadersOfCourse;

    /**
     * Stores the course units that belong to the course
     */
    private List<CourseUnit> courseUnitsOfCourse;

    /**
     * This attribute represents a pagination object. It stores all the
     * information that is necessary for pagination, e.g. the number of elements
     * per page.
     */
    private PaginationData pagination;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    private boolean editMode;
    private int courseID;

    /**
     * Saves the edited course data and sets the course details page to its
     * not-editable state. In order to save the edited course, the database
     * entry of the course is updated with the new data and the
     * <code>courseDetails</code> page is returned.
     * 
     * @return link to the next page
     */
    public String saveCourse() {
    	if (getEditMode()) {    	    
    	    //CourseDAO.updateCourse(transaction, course);
            setEditMode(false);
    	}
	return "#";
    }

    /**
     * Initializes the course details page with the details of the course that
     * is to display.
     */
    public void initializeCourseDetails() {
    	if (courseID > 0) {
    		course = CourseDAO.getCourse(transaction, courseID);    		
    	}
    }

    public String enableEditMode() {
        setEditMode(true);
        return "#";
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
     *            the new course
     */
    public void setCourse(Course course) {
    }

    /**
     * Returns whether the user has registered for course news.
     * 
     * @return whether the user has registered for course news
     */
    public boolean registeredForCourseNews() {
	return registeredForCourseNews;
    }

    /**
     * Signs a user up for a course and returns the updated page.<br>
     * Signing up for the course means that the user is set as participant of
     * the course and the course is added to the courses of the user. Also the
     * database entry of the course is updated.
     * 
     * <p>
     * If there goes something wrong during the sign up action, a
     * CourseRegistrationException is thrown.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occurs during the sign up process
     */
    public String signUpForCourse() throws CourseRegistrationException {
        //I have to start a transaction for database comunication
        Transaction trans = Connection.create();
        trans.start();
        
        
        try{ 
            //First i fetch the number of Participants and the maximum amount a Course can handle
            int numberOfParticipants = CourseDAO.getNumberOfParticipants(trans, courseID);
            Course courseToSignUp = CourseDAO.getCourse(trans, courseID);
            
            if(courseToSignUp.getMaxUsers() > numberOfParticipants){
                
                //Add user to course_participant list on the database server
                CourseDAO.addUserToCourse(trans, sessionUser.getUserID(), courseID);
                if(registeredForCourseNews){
                    CourseDAO.addUserToInformUser(trans, sessionUser.getUserID(), courseID);
                }
                LogHandler.getInstance().debug("User:"+ sessionUser.getUserID() + "Succesfull signed for course:" + courseID);
                
            }else{
               //If the course is full we throw the CourseRegistrationException
               throw new CourseRegistrationException(); 
            }
        } catch(InvalidDBTransferException e){
            //TODO Error Handling
            LogHandler.getInstance().error("Error occured while User:" + sessionUser.getUserID() + " signing up for course:" + courseID);
        }
        
        
        
        
        
        
        
        
	return "#";
    }



    /**
     * Signs a user off from a course and returns the updated page.<br>
     * Signing off from the course means that the user is removed as participant
     * of the course and the course is removed from the courses of the user.
     * Also the database entry of the course is updated.
     * 
     * <p>
     * If there goes something wrong during the sign off action a
     * CourseRegistrationException is thrown.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occours during the sign off process
     */
    public String signOffFromCourse() throws CourseRegistrationException {
	return "#";
    }

    /**
     * Selects all avialible course units of a course at once.
     */
    public void selectAllCourseUnits() {
    }

    /**
     * Signs a user up for a course unit and returns the updated page.<br>
     * Signing up for the course unit means that the user is set as participant
     * of the course unit and the course unit is added to the course units of
     * the user. Also the database entry of the course unit is updated.
     * 
     * <p>
     * The user can only sign up for the course unit if it's not already full
     * and if the user has enough money on his account to pay for the unit.
     * During the signing up action the price of the course unit is
     * automatically removed from the account of the user. If there goes
     * something wrong during the sign up action, a CourseRegistrationException
     * is thrown and the paid price is transfered back to the account of the
     * user.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occurs during the sign up process
     */
    public String signUpForCourseUnits() throws CourseRegistrationException {
	return null;
    }

    /**
     * Signs a user off from a course unit and returns the updated page.<br>
     * Signing off from the course unit means that the user is removed as
     * participant of the course unit and the course unit is removed from the
     * course units of the user. Also the database entry of the course unit is
     * updated.
     * 
     * <p>
     * The user can only sign off from the course unit if he is signed in.
     * During the signing off action the price of the course unit is
     * automatically transfered to the account of the user. If there goes
     * something wrong during the sign up action, a CourseRegistrationException
     * is thrown.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occurs during the sign off process
     */
    public String signOffFromCourseUnits() throws CourseRegistrationException {
	return null;
    }

    /**
     * Returns the value of the attribute <code>selectedCourseUnits</code>.
     * 
     * @return list of selected course units
     */
    public List<CourseUnit> getSelectedCourseUnits() {
	return selectedCourseUnits;
    }

    /**
     * Sets the value of the attribute <code>selectedCourseUnits</code>.
     * 
     * @param selectedCourseUnits
     *            the new list of selected course units
     */
    public void setSelectedCourseUnits(List<CourseUnit> selectedCourseUnits) {
    }

    /**
     * Returns the value of the attribute <code>leadersOfCourse</code>.
     * 
     * @return list of leaders
     */
    public List<User> getLeadersOfCourse() {
	return leadersOfCourse;
    }

    /**
     * Sets the value of the attribute <code>leadersOfCourse</code>.
     * 
     * @param leaders
     *            the new list of leaders
     */
    public void setLeadersOfCourse(List<User> leaders) {
    }

    /**
     * Returns the value of the attribute <code>courseUnitsOfCourse</code>.
     * 
     * @return list of course units
     */
    public List<CourseUnit> getCourseUnitsOfCourse() {
	return courseUnitsOfCourse;
    }

    /**
     * Sets the value of the attribute <code>courseUnitsOfCourse</code>.
     * 
     * @param courseUnits
     *            the new list of course units
     */
    public void setCourseUnitsOfCourse(List<CourseUnit> courseUnits) {
    }

    /**
     * Redirect the user to a page where the participants of the course are
     * displayed
     * 
     * @return link to the next page
     */
    public String loadParticipientsPage() {
	return null;
    }

    /**
     * Redirect the user to a page where he can create a new course unit.
     * 
     * @return link to the next page
     */
    public String loadCreateCourseUnitPage() {
	return null;
    }

    /**
     * Redirects the user to a page where he can edit a course unit.
     * 
     * @return link to the next page
     */
    public String loadEditCourseUnitPage() {
	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	// TODO Auto-generated method stub
    }
    
    

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginationData getPagination() {
	return pagination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPagination(PaginationData pagination) {
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
    }

    /**
     * Returns the edit mode flag.
     * 
     * @return true when in edit mode, false if not
     */
	public boolean getEditMode() {
		return editMode;
	}

	/**
	 * Set-method for the edit mode flag.
	 * 
	 * @param editMode the boolean flag to be set
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	/**
	 * Returns the course's id.
	 * 
	 * @return the courseID
	 */
	public int getCourseID() {
		return courseID;
	}

	/**
	 * Set-method for the course's id.
	 * 
	 * @param courseID the courseID to set
	 */
	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

}
