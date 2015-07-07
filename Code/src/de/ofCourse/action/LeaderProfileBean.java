/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.List;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;


import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Displays the profile of a course instructor.
 * <p>
 * This class is ManagedBean and controller of the facelet <code>leaderProfile</code>.
 * 
 * @author Ricky Strohmeier
 *
 */
@ManagedBean
@ViewScoped
public class LeaderProfileBean implements Pagination, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5756395056323366426L;

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the displayed or entered data of the user
     */
    private User user;
    
    /**
     * Stores the managed courses of a user in case of the user is a course
     * leader
     */
    private List<Course> managedCourses;
    
    private Part image;
    
    private int userID;

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

    private int currentPage;

    private static int pageElements = 10;
    
    /**
     * Init-method for the User or Leader Profile Page.
     * 
     * @author Ricky Strohmeier
     */
    @PostConstruct
    private void init() {
        pagination = new PaginationData(pageElements, 0, SortColumn.TITLE, SortDirection.ASC);

    	transaction = Connection.create();

    	try {
    	    transaction.start();
    	    userID = Integer.parseInt(FacesContext.getCurrentInstance()
    	                .getExternalContext().getRequestParameterMap().get("userID"));
    	    user = UserDAO.getUser(transaction, userID);
            pagination.refreshNumberOfPages(UserDAO.getNumberOfCoursesLeadedBy(transaction, userID));
            managedCourses = UserDAO.getCoursesLeadedBy(transaction, userID, pagination);

    		transaction.commit();
    	} catch (InvalidDBTransferException e) {
    		LogHandler.getInstance().error("SQL Exception occoured during executing init() in UserProfileBean");
    		transaction.rollback();
    	}
    }

    /**
     * Returns the value of the attribute <code>user</code>.
     * 
     * @return the actual displayed user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the value of the attribute <code>user</code>.
     * 
     * @param userToSet
     *            the displayed user
     */
    public void setUser(User userToSet) {
        this.user = userToSet;
    }

    /**
     * Returns the value of the attribute <code>managedCourses</code>.
     * 
     * @return list of courses that are managed by a course leader
     */
    public List<Course> getManagedCourses() {
        return managedCourses;
    }

    /**
     * Sets the value of the attribute <code>managedCourses</code> that stores a
     * list of courses that are managed by a course leader.
     * 
     * @param managedCourses
     *            list of courses that are managed by a course leader
     */
    public void setManagedCourses(List<Course> managedCourses) {
        this.managedCourses = managedCourses;
    }

	public Part getImage() {
		return image;
	}

	public void setImage(Part image) {
		this.image = image;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
        pagination.setCurrentPageNumber(currentPage);
        transaction.start();
        try {
            managedCourses = UserDAO.getCoursesLeadedBy(transaction, userID, pagination);
            transaction.commit();
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error("Error occured during fetching data for pagination.");
            this.transaction.rollback();
        }
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
    public void setSessionUser(SessionUserBean sessionUser) {
    	this.sessionUser = sessionUser;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
    }
    
    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    /**
     * @return the userID
     */
    public String getUserIDString() {
        return "" + userID;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
