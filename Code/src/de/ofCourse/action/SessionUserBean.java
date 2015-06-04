/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.ofCourse.model.Language;
import de.ofCourse.model.UserRole;
import de.ofCourse.model.UserStatus;

/**
 * Represents the management of the session of a logged in user.<p>
 * This Bean contains the id, the userRole, the userStatus of the user and the
 * selected language. It is available during the complete session.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean(name="sessionUser")
@SessionScoped
public class SessionUserBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Unique id, which represents an user in the system
     */
    private int userID;

    /**
     * Status of the user
     */
    private UserStatus userStatus;

    /**
     * Role of the user
     */
    private UserRole userRole;

    /**
     * The current language
     */
    private Language language;

    /**
     * Returns the ID of the current logged in user.
     * 
     * @return userID of the logged in user
     */
    public int getUserID() {
	return userID;
    }

    /**
     * Sets the ID of the logged-in user.
     * 
     * @param userId
     *            ID of user
     */
    public void setUserID(int userID) {
	this.userID = userID;
    }

    /**
     * Returns the status of the user
     * 
     * @return status of user
     */
    public UserStatus getUserStatus() {
	return userStatus;
    }

    /**
     * Sets the status of an user in the system.
     * 
     * @param userStatus
     *            status of user
     */
    public void setUserStatus(UserStatus userStatus) {
	this.userStatus = userStatus;
    }

    /**
     * Returns the role of an user in system.
     * 
     * @return role of user
     */
    public UserRole getUserRole() {
	return userRole;
    }

    /**
     * Sets the role of an user in the system.
     * 
     * @param userRole
     *            role of user
     */
    public void setUserRole(UserRole userRole) {
	this.userRole = userRole;
    }

    /**
     * Returns the language of the system
     * 
     * @return the language
     */
    public Language getLanguage() {
	return language;
    }

    /**
     * Sets the language of the system
     * 
     * @param language
     *            the language to set
     */
    public void setLanguage(Language language) {
	this.language = language;
    }
}
