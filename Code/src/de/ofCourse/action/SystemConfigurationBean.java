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
     * Stores the user whose account is to be toped up
     */
    private User userToTopUp;

    /**
     * Stores the type of account activation that was selected by the
     * administrator.
     */
    private int accountActivationType;

    /**
     * Represents the type of activation: email verification
     */
    private static final int type_EMAILVERIFICATION = 1;

    /**
     * Represents the type of activation: email verification and activation by a
     * course leader
     */
    private static final int type_ADMIN_ACTIVATION = 2;

    /**
     * Represents the type of activation: email verification and activation by a
     * administrator
     */
    private static final int type_EMAILVERIFICATION_AND_ADMIN = 3;

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
	this.overdraftCredit = (float) 0.00;
    }

    private void topUpUserAccount() {

    }

    private double amountToTopUp;

    /**
     * Determines the type of account activation, that means it updates the
     * setting relating to account activation in the database.
     */
    public void determineAccountActivationType() {
	System.out.println(this.accountActivationType);
    }

    /**
     * Returns the value of the attribute <code>accountActivationType</code>
     * that stores the selected type of account activation.
     * 
     * @return the selected account activation type
     */
    public int getAccountActivationType() {
	return accountActivationType;
    }

    /**
     * Sets the value of the attribute <code>accountActivationType</code> that
     * stores the selected type of account activation.
     * 
     * @param accountActivationType
     *            the new type of account activation
     */
    public void setAccountActivationType(int accountActivationType) {
	this.accountActivationType = accountActivationType;
    }

    /**
     * Determines the granted overdraft credit, that means it updates the
     * setting relating to overdraft credit in the database.
     */
    public void determineOverdraftCredit() {
	this.transaction.start();

	try {
	    SystemDAO.setSignOffLimit(transaction, this.signOffLimit);
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
    public double getOverdraftCredit() {
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
