/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import de.ofCourse.databaseDAO.SystemDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Activation;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides the functionalities for the administrator relating to the system,
 * like registration management or payment settings.
 * 
 * <p>
 * Furthermore it is responsible for directing the user to sites where the
 * administrator can manage the users of the system, like creating new users or
 * to sites where he can manage the courses of the system, like creating a new
 * course.
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>adminManagement</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@RequestScoped
public class SystemConfigurationBean implements Serializable {

    /**
     * Serial id
     */
    private static final long serialVersionUID = 3365915734550887541L;
    
    /**
     * Represents the url to the page where a administrator can search for users
     */
    private final static String URL_SEARCH_USER = 
	    "/facelets/user/systemAdministrator/searchUser.xhtml";   
    
    /**
     * Represents the url to the page where a new user can be created
     */
    private final static String URL_CREATE_USER = 
	    "/facelets/user/systemAdministrator/createUser.xhtml";
    
    /**
     * Represents the url to the page where a new course can be created
     */
    private final static String URL_CREATE_COURSE = 
	    "/facelets/user/systemAdministrator/createCourse.xhtml";
    
    /**
     * Represents the url to the page where you can search for courses
     */
    private final static String URL_SEARCH = "/facelets/open/courses/search.xhtml";

    /**
     * Stores the overdraft credit that was granted by the administrator
     */
    private float overdraftCredit;


    /**
     * Stores the type of account activation that was selected by the
     * administrator.
     */
    private String accountActivationType;


    /**
     * Stores the sign off limit in hours
     */
    private int signOffLimit;

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user an the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * Initializes the bean with the necessary values to run
     */
    @PostConstruct
    private void init() {
	transaction = Connection.create();
	transaction.start();
	
	try{
	    overdraftCredit = SystemDAO.getOverdraftCredit(transaction);
	    signOffLimit = SystemDAO.getSignOffLimit(transaction);
	    transaction.commit();
	}
	catch(InvalidDBTransferException e){
	    LogHandler.getInstance().error(
		    "Error during initializing admin managements page");
	    transaction.rollback();
	}
	
    }

    /**
     * Determines the type of account activation, that means it updates the
     * setting relating to account activation in the database.
     */
    public void determineAccountActivationType() {
	transaction.start();
	
	try {    
	    
	    SystemDAO.setActivationType(
		    transaction, 
		    Activation.fromString(getAccountActivationType()));    
	    transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during setting"
			    + " the granted overdraft credit.");
	    transaction.rollback();
	}
    }

    /**
     * Returns the value of the attribute <code>accountActivationType</code>
     * that stores the selected type of account activation.
     * 
     * @return the selected account activation type
     */
    public String getAccountActivationType() {
	return accountActivationType;
    }

    /**
     * Sets the value of the attribute <code>accountActivationType</code> that
     * stores the selected type of account activation.
     * 
     * @param accountActivationType
     *            the new type of account activation
     */
    public void setAccountActivationType(String accountActivationType) {
	this.accountActivationType = accountActivationType;
    }

    /**
     * Determines the granted overdraft credit, that means it updates the
     * setting relating to overdraft credit in the database.
     */
    public void determineOverdraftCredit() {
	transaction.start();

	try {
	    SystemDAO.setOverdraftCredit(transaction, overdraftCredit);
	    transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during setting"
			    + " the granted overdraft credit.");
	    transaction.rollback();
	}
    }

    /**
     * Returns the value of the attribute <code>overdraftCredit</code> that
     * stores the granted overdraft credit.
     * 
     * @return the granted credit
     */
    public float getOverdraftCredit() {
	return overdraftCredit;
    }

    /**
     * Sets the value of the attribute <code>overdraftCredit</code> that stores
     * the the granted credit.
     * 
     * @param overdraftCredit
     *            the new overdraft credit
     */
    public void setOverdraftCredit(float overdraftCredit) {
	this.overdraftCredit = overdraftCredit;
    }

    /**
     * Determines the granted overdraft credit, that means it updates the
     * setting relating to overdraft credit in the database.
     */
    public void determineSignOffLimit() {
	transaction.start();

	try {
	    SystemDAO.setSignOffLimit(transaction, signOffLimit);
	    transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during setting"
			    + " the sign off limit for course units.");
	    transaction.rollback();
	}
    }

    /**
     * Returns the sign off limit for course units
     * 
     * @return the sign off limit.
     */
    public int getSignOffLimit() {
	return signOffLimit;
    }

    /**
     * Sets the sign off limit for course units.
     * 
     * @param signOffLimit
     *            the sign off limit
     */
    public void setSignOffLimit(int signOffLimit) {
	this.signOffLimit = signOffLimit;
    }

    /**
     * Redirects the user to a page where a new user can be created.
     * 
     * @return link to <code>createUser</code> page
     */
    public String loadCreateNewUserPage() {
	return URL_CREATE_USER;
    }

    /**
     * Redirects the user to a page where he can managed the users of the
     * system.
     * 
     * @return link to next page
     */
    public String loadManageUserPage() {
	return URL_SEARCH_USER;
    }

    /**
     * Redirects the user to a page where a new course can be created.
     * 
     * @return link to <code>createCourse</code> page
     */
    public String loadCreateNewCoursePage() {
	return URL_CREATE_COURSE;
    }

    /**
     * Redirects the user to a page where he can managed the courses of the
     * system.
     * 
     * @return link to next page
     */
    public String loadManageCoursesPage() {
	return  URL_SEARCH;
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
