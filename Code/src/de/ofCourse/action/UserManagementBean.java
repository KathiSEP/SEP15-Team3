/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.faces.bean.ManagedProperty;

import de.ofCourse.model.User;
import de.ofCourse.system.Transaction;

/**
 * Provides functionality for administrators to create and delete users.
 * 
 * <p>
 * Only the administrator is able to use this functionalities.<br>
 * Especially only he has the rights to create a new user without passing
 * through the registration process.<br>
 * Also the administrator is the only one who has the right to delete a user
 * inrevocable from the system. This is determined this way because so only the
 * administrator has the right to decide whether he deletes the user and abates
 * possible depts.
 * 
 * @author Tobias Fuchs
 *
 */
public class UserManagementBean {
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * 
     * Stores the entered or displayed data of the user.
     */
    private User user;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * Creates a new user with the entered data and returns the profile page of
     * the new created user.<br>
     * That means that the method creates a new database entry for the users and
     * stores it in the database.
     * 
     * @return link to the next page
     */
    public String createUser() {
	return null;
    }

    /**
     * Uploads a selected picture file from the local system to the server. The
     * picture needs to be a .jpg <br>
     */
    public void uploadProfilPic() {
    }

    /**
     * Deletes the actual displayed user from the system and returns the link to
     * the next page.<br>
     * That means that the method deletes the user from the database. Also the
     * user is signed off from all courses and course units the user attends.
     * 
     * @return link to next page
     */
    public String deleteUser() {
	return null;
    }

    
    /**
     * Returns the value of the attribute <code>user</code>.
     * 
     * @return the displayed user
     */
    public User getUser() {
	return user;
    }

    /**
     * Sets the value of the attribute <code>user</code>.
     * 
     * @param user
     *            the displayed user
     */
    public void setUser(User user) {
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

}
