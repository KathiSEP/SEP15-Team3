/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.Part;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.model.Address;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

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
 * @author Patrick Cretu
 *
 */
@ManagedBean
@RequestScoped
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
    
    private String password;
    
    private String confirmPassword;
    
    private Part image;
    
    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    public void init() {
    	image = null;
    	this.user = new User();
        this.user.setAddress(new Address());
    }
    
    /**
     * Creates a new user with the entered data and returns the profile page of
     * the new created user.<br>
     * That means that the method creates a new database entry for the users and
     * stores it in the database.
     * 
     * @return link to the next page
     */
    public String createUser() {
    	transaction = Connection.create();
    	transaction.start();
    	String salt = generateSalt();
        String pwHash = PasswordHash.hash(password, salt);
        
        if (UserDAO.emailExists(transaction, user.getEmail())) {
            FacesMessageCreator.createFacesMessage(null, "E-Mail bereits vergeben");
            this.transaction.rollback();
        } else if (UserDAO.nickTaken(transaction, user.getUsername())) {
        	FacesMessageCreator.createFacesMessage(null, "Benutzername bereits vergeben");
            this.transaction.rollback();
        } else {
        	String veriString = UserDAO.createUser(this.transaction, user, pwHash, salt);
        	int userID = UserDAO.getUserID(this.transaction, user.getUsername());
        	UserDAO.verifyUser(transaction, veriString);
        	if (image != null) {
        		UserDAO.uploadImage(transaction, userID, image);
        	}
        	transaction.commit();
        }
    	return "/facelets/user/systemAdministrator/listUsers.xhtml?faces-redirect=false";
    }
    
    private String generateSalt() {
    	return String.valueOf(System.currentTimeMillis() * Math.random());
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

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public Part getImage() {
		return image;
	}

	public void setImage(Part image) {
		this.image = image;
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
