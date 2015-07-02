package de.ofCourse.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * 
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@ViewScoped
public class LeadedCoursesBean implements Pagination, Serializable {
    
    
    /**
     * Serial id
     */
    private static final long serialVersionUID = 8760737542397078280L;

    /**
     * This attribute represents a pagination object. It stores all the
     * information that is necessary for pagination, e.g. the number of elements
     * per page.
     */
    private PaginationData pagination;
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    /**
     * Number of elements that are to display display with pagination at once
     */
    private static final int elementsPerPage = 10;

    /**
     * List of courses that the user leads
     */
    private List<Course> leadedCourses;

    /**
     * Parameter by which is sorted
     */
    private String orderParam;


    /**
     * Represents the fetched number of the current displayed page.
     */
    private int currentPage;

    /**
     * Represents the url to the course detail page
     */
    private final static String URL_COURSE_DETAIL =
	    "/facelets/open/courses/courseDetail.xhtml";
    
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
    private void init(){
	leadedCourses = new ArrayList<Course>();
	pagination = new PaginationData(
		elementsPerPage,
		0,
		SortColumn.TITLE,
		SortDirection.ASC);

	//Setting up transaction
	transaction = Connection.create();
	transaction.start();

	//Initializing the page
	try {
	    pagination.refreshNumberOfPages(
		    UserDAO.getNumberOfCoursesLeadedBy(
			    transaction,
			    sessionUser.getUserID()));
	    
	    leadedCourses = UserDAO.getCoursesLeadedBy(transaction,
		    					sessionUser.getUserID(),
		    					getPagination());
	    transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error occured during updating the"
		    + " page with elements from database.");
	}	
    }
    
    
    /**
     * Redirects the user to the <code>courseDetail</code> page of the selected
     * course.
     * 
     * @return link to the <code>courseDetail</code> page
     */
    public String loadCourseDetailsPageOfSelectedCourse() {
	return URL_COURSE_DETAIL;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
	pagination.setCurrentPageNumber(currentPage);
	transaction.start();
	
	try {
	    leadedCourses = UserDAO.getCoursesLeadedBy(transaction,
			sessionUser.getUserID(),
			getPagination());
	    transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error occured during fetching data for pagination.");
	}
	
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	//Compute sort direction
		if(getPagination().getSortColumn().equals(
			                          SortColumn.fromString(orderParam))) {
		     
		    getPagination().changeSortDirection();
		} else {
		    getPagination().setSortColumn(SortColumn.fromString(orderParam));
		}
		
		//Fetch the needed courses
		transaction.start();
	    	try {
	    	 leadedCourses = UserDAO.getCoursesLeadedBy(transaction,
				sessionUser.getUserID(),
				getPagination());
	    	    transaction.commit();
	    	    
	    	} catch (InvalidDBTransferException e) {
		    transaction.rollback();
		    LogHandler.getInstance().error(
			    "Error occured during sorting my courses");
		}
	
    }

    /**
     * Returns the order parameter.
     * 
     * @return the order parameter
     */
    public String getOrderParam() {
	return orderParam;
    }

    /**
     * Sets the order parameter.
     * 
     * @param orderParam
     *            the order s to set
     */
    public void setOrderParam(String orderParam) {
	this.orderParam = orderParam;
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
     * Returns the current displayed page number.
     * 
     * @return the page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Set the current displayed page number. 
     * 
     * @param currentPage the page number to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    
    /**
     * Returns a list of courses that the user leads
     * 
     * @return list of courses the user leads
     */
    public List<Course> getLeadedCourses() {
        return leadedCourses;
    }



    /**
     * Sets the value of the attribute <code>leadedCourses</code>.
     * 
     * @param leadedCourses
     *            list of courses the user leads
     */
    public void setLeadedCourses(List<Course> leadedCourses) {
        this.leadedCourses = leadedCourses;
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
}
