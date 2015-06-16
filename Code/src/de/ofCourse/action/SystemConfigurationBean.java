/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import de.ofCourse.Database.dao.SystemDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Activation;
import de.ofCourse.model.User;
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
	
	this.transaction = Connection.create();
	transaction.start();
	try{
	this.overdraftCredit = SystemDAO.getOverdraftCredit(transaction);
	this.signOffLimit = SystemDAO.getSignOffLimit(transaction);
	this.transaction.commit();
	}
	catch(InvalidDBTransferException e){
	    LogHandler.getInstance().error("Error during initializing admin managements page");
	    this.transaction.rollback();
	}
	
    }

    /**
     * Determines the type of account activation, that means it updates the
     * setting relating to account activation in the database.
     */
    public void determineAccountActivationType() {
	this.transaction.start();
	
	try {
	    switch(this.accountActivationType){
	    case "EMAIL":
		SystemDAO.setActivationType(transaction, Activation.EMAIL);
		break;
	    case "EMAIL_COURSE_LEADER":
		SystemDAO.setActivationType(transaction, Activation.EMAIL_COURSE_LEADER);
		break;
	    case "EMAIL_ADMIN":
		SystemDAO.setActivationType(transaction, Activation.EMAIL_ADMIN);
		break;
	    }
	    
	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during setting"
			    + " the granted overdraft credit.");
	    this.transaction.rollback();
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
	this.transaction.start();

	try {
	    SystemDAO.setOverdraftCredit(transaction, this.overdraftCredit);
	    this.transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during setting"
			    + " the granted overdraft credit.");
	    this.transaction.rollback();
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
	this.transaction.start();

	try {
	    SystemDAO.setSignOffLimit(transaction, this.signOffLimit);
	    this.transaction.commit();
	    
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during setting"
			    + " the sign off limit for course units.");
	    this.transaction.rollback();
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
	return "/facelets/user/systemAdministrator/createUser.xhtml";
    }

    /**
     * Redirects the user to a page where he can managed the users of the
     * system.
     * 
     * @return link to next page
     */
    public String loadManageUserPage() {
	return "/facelets/user/systemAdministrator/listUsers.xhtml";
    }

    /**
     * Redirects the user to a page where a new course can be created.
     * 
     * @return link to <code>createCourse</code> page
     */
    public String loadCreateNewCoursePage() {
	return "/facelets/user/systemAdministrator/createCourse.xhtml";
    }

    /**
     * Redirects the user to a page where he can managed the courses of the
     * system.
     * 
     * @return link to next page
     */
    public String loadManageCoursesPage() {
	return "/facelets/open/courses/search.xhtml";
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
