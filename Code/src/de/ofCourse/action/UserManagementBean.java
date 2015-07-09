/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.Part;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.Salutation;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
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
	 * The URL returned at an unsuccessful attempt of creating a new user
	 */
	private final String URL_CREATE_USER = "/facelets/user/" +
			"systemAdministrator/createUser.xhtml?faces-redirect=false";
	
	/**
	 * The URL returned at the successful creating of a new user
	 */
	private final String URL_ADMIN_MANAGEMENT  = "/facelets/user/systemAdministrator/" +
			"adminManagement.xhtml";
	
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the entered or displayed data of the user.
     */
    private User user;
    
    /**
     * The selected user salutation.
     */
    private String salutation;
    
    /**
     * The selected user role.
     */
    private String role;
    
    /**
     * The entered user password.
     */
    private String password;
    
    /**
     * The entered confirmation password.
     */
    private String confirmPassword;
    
    /**
     * The selected user image.
     */
    private Part image;
    
    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    @PostConstruct
    public void init() {
    	image = null;
    	this.user = new User();
        this.user.setAddress(new Address());
        transaction = Connection.create();
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
    	transaction.start();
        
        try {
	        if (UserDAO.emailExists(transaction, user.getEmail())) {
	            FacesMessageCreator.createFacesMessage(null,
	            		sessionUser.getLabel(
	            				"registerUserBean.facesMessage.EmailExisting"));
	            this.transaction.rollback();
	        } else if (UserDAO.nickTaken(transaction, user.getUsername())) {
	        	FacesMessageCreator.createFacesMessage(null,
	        			sessionUser.getLabel("createUser.username.Message"));
	            this.transaction.rollback();
	        } else {
	        	setEnums();
	        	String salt = PasswordHash.getSalt();
	        	String pwHash = PasswordHash.hash(password, salt);
	        	String veriString = UserDAO.createUser(this.transaction, user,
	        			pwHash, salt);
	        	int userID = UserDAO.getUserID(this.transaction,
	        			user.getUsername());
	        	
	        	user.setUserId(userID);
	        	List<User> activateUser = new ArrayList<User>();
	        	activateUser.add(user);
	        	
	        	UserDAO.AdminActivateUsers(transaction, activateUser);
	        	UserDAO.verifyUser(transaction, veriString);
	        	
	        	if (image != null) {
	        		UserDAO.uploadImage(transaction, userID, image);
	        	}
	        	transaction.commit();
	        	
	        	FacesMessageCreator
                .createFacesMessage(
                        null,
                        sessionUser.getLabel(
                                "createUser.successMessage"));
	        	return URL_ADMIN_MANAGEMENT;
	        }
        } catch (InvalidDBTransferException e) {
        	FacesMessageCreator
            .createFacesMessage(
                    null,
                    sessionUser.getLabel(
                            "profile.message.error"));
        	LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "createUser()");
        	transaction.rollback();
        }
    	return URL_CREATE_USER;
    }
    
    /**
     * Sets the selected user salutation and role in the user object.
     */
    private void setEnums() {
    	if (salutation.equals("mr")) {
    		user.setSalutation(Salutation.MR);
    	} else {
    		user.setSalutation(Salutation.MS);
    	}
    	
    	if (role.equals("admin")) {
    		user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
    	} else if (role.equals("leader")) {
    		user.setUserRole(UserRole.COURSE_LEADER);
    	} else {
    		user.setUserRole(UserRole.REGISTERED_USER);
    	}
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
    	this.user = user;
    }

    /**
     * 
     * @return the selected user salutation
     */
    public String getSalutation() {
		return salutation;
	}

    /**
     * Sets the user salutation
     * 
     * @param salutation
     *                 the selected user salutation
     */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	/**
	 * 
	 * @return the selected user role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the selected user role
	 * 
	 * @param role
	 *           the selected user role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 
	 * @return the entered user password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the entered user password
	 * 
	 * @param password
	 *               the entered user password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return the entered confirmation password
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * Sets the entered confirmation password
	 * 
	 * @param confirmPassword
	 *                      the entered confirmation password
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * 
	 * @return the selected user image
	 */
	public Part getImage() {
		return image;
	}

	/**
	 * Sets the selected user image
	 * 
	 * @param image
	 *            the selected user image
	 */
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
    public void setSessionUser(SessionUserBean sessionUser) {
    	this.sessionUser = sessionUser;
    }

}
