/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
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
 * @author Katharina H�lzl
 *
 */
@ManagedBean
@ViewScoped
public class AccountManagementBean implements Pagination {

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    /**
     * Stores the elements per page
     */
    private static final int elementsPerPage = 10;

    /**
     * Stores the columns to sort
     */
    private String sortColumn;

    /**
     * The users that are selected by the user and shall be activated.
     */
    private List<User> usersToActivate;

    /**
     * The users that are displayed on the page.
     */
    private DataModel<User> users;

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
     * Stores the current page
     */
    private int currentPage;
    
    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @PostConstruct
    private void init() {
        this.users = new ListDataModel<User>();
        pagination = new PaginationData(elementsPerPage, 0, 
                                        SortColumn.NICKNAME, SortDirection.ASC);
        
        transaction = Connection.create();
        transaction.start();
        try {
            this.pagination.refreshNumberOfPages(UserDAO
                          .getNumberOfNotAdminActivatedUsers(this.transaction));
            //Set a table witch users which are not admin activated yet
            this.users.setWrappedData(UserDAO.getNotAdminActivatedUsers(
                                       this.transaction, this.getPagination()));
            this.transaction.commit();
            
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during updating the"
                            + " page with elements from database.");
            this.transaction.rollback();
        }
    }
    
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
        @SuppressWarnings("unchecked")
        List<User> allUsers = (List<User>) this.users.getWrappedData();
        this.usersToActivate = new ArrayList<User>();
        
        for(User user : allUsers) {
            if(user.isSelected()) {
                this.usersToActivate.add(user);
            }
        }
        
        if(this.usersToActivate.size() > 0) {
        
            this.transaction = Connection.create();
            transaction.start();
            try {
                //Check if the user activation by the admin was successful
                if(UserDAO.AdminActivateUsers(this.transaction, 
                                            this.usersToActivate) == false) {
                    LogHandler.getInstance().error(
                            "Error occured during adminActivateUsers().");
                    
                    } else {
                        //Refresh page content
                        this.pagination.refreshNumberOfPages(UserDAO
                          .getNumberOfNotAdminActivatedUsers(this.transaction));
                        this.users.setWrappedData(
                                UserDAO.getNotAdminActivatedUsers
                                                        (this.transaction, 
                                                         this.getPagination()));
                        //FacesMessage: 'User activation successful'
                        FacesMessageCreator.createFacesMessage(
                                 null,
                                 sessionUser.getLabel(
                                         "AccountManagementBean.facesMessage."
                                         + "ActivationSucceed"));
                    }
                    this.transaction.commit();
                
            } catch (InvalidDBTransferException e) {
                LogHandler.getInstance().error(
                        "Error occured during adminActivateUsers().");
                
                this.transaction.rollback();
               }
         } else {
             //FacesMessage: 'No users selected'
            FacesMessageCreator.createFacesMessage(
                    null, 
                    sessionUser.getLabel(
                            "AccountManagementBean.facesMessage.NoUsers"));            
        }
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
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
	this.pagination.setCurrentPageNumber(this.currentPage);
        
        transaction.start();
        
        try {
            this.users.setWrappedData(UserDAO.getNotAdminActivatedUsers(
                    this.transaction, 
                    this.getPagination()));
            
            this.transaction.commit();
            
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during fetching data for pagination.");
            this.transaction.rollback();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
        
        if(this.getPagination().getSortColumn().equals(SortColumn.fromString
                                                          (this.sortColumn))) {
            this.getPagination().changeSortDirection();
            
        } else {
            this.getPagination().setSortColumn(SortColumn.fromString(
                                                           this.sortColumn));
        }
        
        transaction.start();
        
        try {
            this.users.setWrappedData(UserDAO.getNotAdminActivatedUsers(
                    this.transaction, 
                    this.getPagination()));
            
            this.transaction.commit();
            
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during fetching data for pagination.");
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

    /**
     * @return the users
     */
    public DataModel<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(DataModel<User> users) {
        this.users = users;
    }

}
