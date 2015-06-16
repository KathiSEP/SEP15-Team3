/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides functionality for course leaders and administrators to activate user
 * accounts.
 * 
 * <p>
 * Whether the activation by a course leader or an administrator is necessary,
 * is determined by the administrator in his administrator settings. This class
 * uses pagination to display the users.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>activateUsers</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@ViewScoped
public class AccountManagmentBean implements Pagination {

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    private String sortColumn;

    /**
     * The users that are selected by the user and shall be activated.
     */
    private List<User> usersToActivate;

    /**
     * The users that are displayed on the page.
     */
    private List<User> users;

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
     * Activates the user accounts that are selected in the facelet.
     * <p>
     * It gets the users selected by the administrator or the course leader and
     * changes the userStatus of these users to <code>REGISTERED</code>.
     * Furthermore it updates the entries of the users in the database. The
     * system sends a message to all successfully new activated user to inform
     * them about the activation and that they are now able to log in the
     * system.
     */
    public void activateAccounts() {
    }

    /**
     * Returns the value of the attribute <code>usersToActivate</code>.
     * 
     * @return the users to activate
     */
    public List<User> getUsersToActivate() {
	return usersToActivate;
    }

    /**
     * Sets the value of the attribute <code>usersToActivate</code>.
     * 
     * @param usersToActivate
     *            the users that are to be activated
     */
    public void setUsersToActivate(List<User> usersToActivate) {
    }

    /**
     * Returns the value of the attribute <code>users</code>.
     * 
     * @return the displayed users
     */
    public List<User> getUsers() {
	return users;
    }

    /**
     * Sets the value of the attribute <code>users</code>.
     * 
     * @param users
     *            the users that are to be displayed
     */
    public void setUsers(List<User> users) {
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
    public void setSessionUser(SessionUserBean userSession) {
        this.sessionUser = userSession;
    }

    /**
     * @return the sortColumn
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * @param sortColumn the sortColumn to set
     */
    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

}
