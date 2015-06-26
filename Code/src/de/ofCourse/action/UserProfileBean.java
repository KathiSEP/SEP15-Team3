/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.text.DecimalFormat;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Salutation;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * Displays the profile of a user and provides the functionality to edit the
 * data of the user. In case of the user is a course leader the courses managed
 * by the user are displayed as well.
 * <p>
 * The user can edit his complete userdata except his id, his user role and his
 * account balance.<br>
 * Also it is possible to set himself <code>INACTIVE</code>. For the user it
 * seems that he has deleted his account. In reality he is only set inactive and
 * cannot log in anymore.<br>
 * That is necessary because so it is impossible for the user to delete his
 * depts or balance if he wants to delete his account.<br>
 * A real delete of a user only can be done by the administrator.
 * <p>
 * This class is ManagedBean and controller of the facelet <code>userProfil</code>.
 * 
 * @author Patrick Cretu
 *
 */
@ManagedBean
@ViewScoped
public class UserProfileBean {
    
	/**
	 * The URL which is returned if an administrator unsuccessfully edits the
	 * user's data
	 */
	private final String URL_PROFILE = "/facelets/user/systemAdministrator/" +
			"profile.xhtml?faces-redirect=false";
	
	/**
	 * The URL which is returned if an administrator successfully edits the
	 * user's data
	 */
	private final String URL_ADMIN_MANAGEMENT = "/facelets/user/" +
			"systemAdministrator/adminManagement.xhtml?faces-redirect=true";
	
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the displayed or entered data of the user
     */
    private User user;
    
    /**
     * Checks if the user requests to edit his data
     */
    private boolean readOnly;
    
    /**
     * The entered user password
     */
    private String password;
    
    /**
     * The entered confirmation password
     */
    private String confirmPassword;
    
    /**
     * The selected user image
     */
    private Part image;
    
    /**
     * The user's ID
     */
    private int userID;

    /**
     * The selected user salutation
     */
    private String salutation;
    
    /**
     * The selected user role
     */
    private String role;
    
    /**
     * The user's credit balance
     */
    private String creditBalance;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    @PostConstruct
    public void init() {
    	readOnly = true;

    	transaction = Connection.create();

    	try {
    	    transaction.start();
    	    userID = Integer.parseInt(FacesContext.getCurrentInstance()
    	                .getExternalContext().getRequestParameterMap()
    	                .get("userID"));
    		user = UserDAO.getUser(transaction, userID);
    		transaction.commit();
    		
    		DecimalFormat f = new DecimalFormat("#0.00");
    		setCreditBalance(f.format(user.getAccountBalance()));
    	} catch (InvalidDBTransferException e) {
    		LogHandler.getInstance().error("SQL Exception occoured during" +
    				"executing init() in UserProfileBean");
    		transaction.rollback();
    	}
    }

    /**
     * Saves the edited user data and sets the user details page to its
     * not-editable state. In order to save the edited user, the database entry
     * of the user is updated with the new data and the <code>userProfil</code>
     * page is returned.
     */
    public void saveSettings() {
    	setEnums();
    	transaction.start();
    	
    	try {
    		User checkUser = UserDAO.getUser(transaction, userID);
    		boolean nickTaken = UserDAO.nickTaken(transaction,
    				user.getUsername());
    		boolean emailTaken = UserDAO.emailExists(transaction,
    				user.getEmail());
    		
    		if (acceptUserInput(checkUser, nickTaken, emailTaken)) {
    			String pwHash = null;
    			String salt = null;
    			if (password != null && password.trim().length() > 0) {
    				salt = PasswordHash.getSalt();
    				pwHash = PasswordHash.hash(password, salt);
    			}
    			UserDAO.updateUser(transaction, user, pwHash, salt);
    			transaction.commit();
    			readOnly = true;
    		} else {
    			FacesMessageCreator.createFacesMessage(null,
    					sessionUser.getLabel("profile.message"));
    			transaction.rollback();
    		}
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "saveSettings()");
    		transaction.rollback();
    	}
    }
    
    /**
     * Sets the selected user salutation and role in the user object.
     */
    private void setEnums() {
    	if (salutation != null) {
    		if (salutation.equals("ms")) {
    		user.setSalutation(Salutation.MS);
	    	} else {
	    		user.setSalutation(Salutation.MR);
	    	}
    	}
    	
    	if (role != null) {
    		if (role.equals("admin")) {
    		user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
	    	} else if (role.equals("leader")) {
	    		user.setUserRole(UserRole.COURSE_LEADER);
	    	} else {
	    		user.setUserRole(UserRole.REGISTERED_USER);
	    	}
    	}
    }
    
    /**
     * Checks whether or not the entered user nickname and e-mail address are
     * valid.
     * 
     * @param checkUser
     *                the user of whom the data is checked
     * @param nickTaken
     *                the value if the entered nickname is already taken
     * @param emailTaken
     *                 the value if the entered e-mail address is already taken
     * @return true, if the user input is valid, false otherwise
     */
    private boolean acceptUserInput(User checkUser, boolean nickTaken,
    		boolean emailTaken) {
    	boolean check = false;
    	boolean sameEmail = checkUser.getEmail().equals(user.getEmail());
    	boolean sameNick = checkUser.getUsername().equals(user.getUsername());
    	
    	if (sameEmail && sameNick) {
    		check = true;
    	} else if(sameEmail && !sameNick && !nickTaken) {
    		check = true;
    	} else if (!sameEmail && !emailTaken && sameNick) {
    		check = true;
    	} else if (!sameEmail && !emailTaken && !sameNick && !nickTaken) {
    		check = true;
    	}
    	return check;
    }
    
    /**
     * Deletes the user from the database.
     * 
     * @return the subsequent URL
     */
    public String deleteUser() {
    	String goToPage = URL_PROFILE;
    	transaction.start();
    	
    	try {
    		UserDAO.delete(transaction, userID, true);
    		transaction.commit();
    		
    		goToPage = URL_ADMIN_MANAGEMENT;
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "deleteUser()");
    		transaction.rollback();
    	}
    	return goToPage;
    }

    /**
     * Uploads a selected picture file from the local system to the server. The
     * picture needs to be a .jpg <br>
     */
    public void uploadProfilePic() {
    	if (image != null) {
	    	transaction.start();
	    	try {
	    		UserDAO.uploadImage(transaction, user.getUserID(), image);
	    		transaction.commit();
	    	} catch (InvalidDBTransferException e) {
	    		LogHandler
	            .getInstance()
	            .error("SQL Exception occoured during executing "
	                    + "uploadProfilePic()");
	            this.transaction.rollback();
	        }
    	}
    }
    
    /**
     * Deletes the user's profile image from the database.
     */
    public void deleteProfilePic() {
    	transaction.start();
    	try {
    		UserDAO.delete(transaction, user.getUserID(), false);
    		transaction.commit();
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "deleteProfilePic()");
            this.transaction.rollback();
        }
    }

    /**
     * Returns the value of the attribute <code>user</code>.
     * 
     * @return the actual displayed user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the value of the attribute <code>user</code>.
     * 
     * @param userToSet
     *            the displayed user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 
     * @return the value of "readOnly"
     */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Sets the value of "readOnly"
	 * 
	 * @param readOnly
	 *               the boolean value
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * 
	 * @return the password value as a string
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password value
	 * 
	 * @param password
	 *               the password value
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return the confirmation password's value
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * Sets the confirmation password's value
	 * 
	 * @param confirmPassword
	 *                      the confirmation password's value
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
    
    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    /**
     * @return the userID
     */
    public String getUserIDString() {
        return "" + userID;
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
	 *                 the user salutation
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
	 * Sets the user role
	 * 
	 * @param role
	 *           the user role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 
	 * @return the user's credit balance
	 */
	public String getCreditBalance() {
		return creditBalance;
	}

	/**
	 * Sets the user's credit balance
	 * 
	 * @param creditBalance
	 *                    the user's credit balance
	 */
	public void setCreditBalance(String creditBalance) {
		this.creditBalance = creditBalance;
	}
	
}
