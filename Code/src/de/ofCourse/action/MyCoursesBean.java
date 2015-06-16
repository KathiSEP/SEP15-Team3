/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Displays the courses that are attended by a certain user and offers the user
 * the possibility to go to the details pages pages of the courses he attends.
 * <p>
 * 
 * Supports pagination to display the courses.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>myCourses</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@ViewScoped
public class MyCoursesBean implements Pagination, Serializable {

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Serial id of MyCoursesBean
     */
    private static final long serialVersionUID = 1L;

    /**
     * Number of elements that are to display display with pagination at once
     */
    private static final int elementsPerPage = 10;

    /**
     * List of courses that the user attends
     */
    private ArrayList<Course> registeredCourses;

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

    /**
     * Initializes the data needed for pagination, the transaction and
     * initializes the page with data sets.
     */
    @PostConstruct
    private void init() {
	this.registeredCourses = new ArrayList<Course>();
	pagination = new PaginationData(elementsPerPage, 0, "title", true);
	
	transaction = Connection.create();
	transaction.start();
	
	try {
	    this.pagination.actualizeNumberOfPages(CourseDAO
		    .getNumberOfMyCourses(transaction,
			    this.sessionUser.getUserID()));
	    this.registeredCourses = (ArrayList<Course>) CourseDAO
		    .getCoursesOf(transaction, this.getPagination(),
			    this.sessionUser.getUserID());
	    this.transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during updating the"
			    + " page with elements from database.");
	    this.transaction.rollback();
	}

    }

    /**
     * Redirects the user to the <code>courseDetail</code> page of the selected
     * course.
     * 
     * @return link to the <code>courseDetail</code> page
     */
    public String loadCourseDetailsPageOfSelectedCourse() {
	return "/facelets/open/courses/courseDetail.xhtml";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
	this.pagination.setCurrentPageNumber(Integer.parseInt(FacesContext
		.getCurrentInstance().getExternalContext()
		.getRequestParameterMap().get("site")));
	transaction.start();
	transaction.start();
	try {
	    this.registeredCourses = (ArrayList<Course>) CourseDAO
		    .getCoursesOf(transaction, this.getPagination(),
			    this.sessionUser.getUserID());
	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fething data for pagination.");
	    this.transaction.rollback();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	// Not needed in MyCoursesBean
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
	this.pagination = pagination;
    }

    /**
     * Returns a list of courses that the user attends
     * 
     * @return list of courses the user attends
     */
    public ArrayList<Course> getRegisteredCourses() {
	return registeredCourses;
    }

    /**
     * Sets the value of the attribute <code>registeredCourses</code>.
     * 
     * @param registeredCourses
     *            list of courses the user attends
     */
    public void setRegisteredCourses(ArrayList<Course> registeredCourses) {
	this.registeredCourses = registeredCourses;
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

}