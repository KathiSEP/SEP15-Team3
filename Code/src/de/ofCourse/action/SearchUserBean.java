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

import org.apache.catalina.tribes.group.interceptors.OrderInterceptor;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides the service of searching for users in the system.<br>
 * The user can search for users by selecting certain parameters and entering a
 * search term for the parameter. Only the administrator has the possibility to
 * search for users in the system.<br>
 * This class supports pagination to display the found users.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>listUsers</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@ViewScoped
public class SearchUserBean implements Pagination {
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the the search result that is displayed on the page. In this case
     * it's a list of users.
     */
    private ArrayList<User> searchResult;
    
    private String displayPeriod;

    

    /**
     * Stores the search parameter that was selected by the user
     */
    private String searchParam;

    /**
     * Stores the search term that was entered by the user
     */
    private String searchString;

    private boolean renderTable = true;
    

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
    
    @PostConstruct
    public void init(){
        searchParam = "name";
        pagination = new PaginationData();
        pagination.setElementsPerPage(10);
        pagination.setSortAsc(true);
    }

    /**
     * Searches for users in the system according to the selected search
     * parameter and the respective search term. <br>
     * It performs a database request with the entered search parameter and
     * search term. The search result is stored locally so it can be displayed
     * in the facelet.
     */
    public void search() {
    }

    /**
     * Returns the value of the attribute <code>searchResult</code> that stores
     * the found database entries which are to be displayed.
     * 
     * @return list of found users
     */
    public ArrayList<User> getSearchResult() {
	return searchResult;
    }

    /**
     * Sets the value of the attribute <code>searchResult</code> that stores the
     * found database entries which are to be displayed.
     * 
     * @param searchResult
     *            list of users found in the database for the actual search
     */
    public void setSearchResult(ArrayList<User> searchResult) {
        this.searchResult = searchResult;
    }

    /**
     * Returns the value of the attribute <code>searchParam</code> that stores
     * the selected search parameter.
     * 
     * @return the selected search parameter
     */
    public String getSearchParam() {
	return searchParam;
    }

    /**
     * Sets the value of the attribute <code>searchParam</code> that stores the
     * selected search parameter.
     * 
     * @param searchParam
     *            the selected search parameter
     */
    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }

    /**
     * Returns the value of the attribute <code>searchTerm</code> that stores
     * the entered search term.
     * 
     * @return the entered search term
     */
    public String getSearchString() {
	return searchString;
    }

    /**
     * Sets the value of the attribute <code>searchTerm</code> that stores the
     * entered search term.
     * 
     * @param searchTerm
     *            the entered search term
     */
    public void setSearchString(String searchTerm) {
        this.searchString = searchTerm;
    }
    
    /**
     * @return the renderTable
     */
    public boolean isRenderTable() {
        return renderTable;
    }

    /**
     * @param renderTable the renderTable to set
     */
    public void setRenderTable(boolean renderTable) {
        this.renderTable = renderTable;
    }

    /**
     * Redirects the user to the <code>userProfil</code> page of a certain user.
     * 
     * @return link to the profile page of the user
     */
    public String loadUserProfilPageOfSelectedUser() {
	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
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
    
    public void displayAllUsers(){
        searchParam = "all";
        transaction = Connection.create();
        transaction.start();
        
        pagination.setCurrentPageNumber(0);
        pagination.setSortAsc(true);
        pagination.setSortColumn("name");
        
        try{
            pagination.actualizeNumberOfPages(UserDAO.getNumberOfUsers(transaction));
            ArrayList<User> result = UserDAO.getUsers(transaction, pagination, searchParam, searchString);
            
            if(result != null){
                searchResult = result;
                setRenderTable(true);
                
                
                transaction.commit();
            }else{
                setRenderTable(false);
                transaction.rollback();
            }
        }catch(InvalidDBTransferException e){
            LogHandler
            .getInstance()
            .error("Error occured during displaYAllUsers");
            transaction.rollback();
        }
        
    }
    


    
}
