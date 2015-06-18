/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import de.ofCourse.Database.dao.CourseUnitDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.Salutation;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * Displays the profile of a user and provides the functionality to edit the data
 * of the user. In case of the user is a course leader the courses managed by
 * the user are displayed as well.
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
public class UserProfileBean implements Pagination {
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the displayed or entered data of the user
     */
    private User user;
    
    /**
     * Stores the managed courses of a user in case of the user is a course
     * leader
     */
    private List<Course> managedCourses;
    
    private boolean readOnly;
    
    private String password;
    
    private String confirmPassword;
    
    private Part image;
    
    private int userID;

    private String salutation;
    
    private String role;

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

    private int currentPage;

    private static int pageElements = 10;
    
    @PostConstruct
    public void init() {
        pagination = new PaginationData(pageElements, 0, "title", true);
    	readOnly = true;
    	password = null;
    	
    	transaction = Connection.create();

    	try {
    	    transaction.start();
    	    userID = Integer.parseInt(FacesContext.getCurrentInstance()
    	            .getExternalContext().getRequestParameterMap().get("userID"));
            pagination.refreshNumberOfPages(UserDAO.getNumberOfCoursesLeadedBy(transaction, userID));
            managedCourses = UserDAO.getCoursesLeadedBy(transaction, userID, pagination);
    		user = UserDAO.getUser(transaction, userID);
    	} catch (InvalidDBTransferException e) {
    		LogHandler.getInstance().error("SQL Exception occoured during executing init() in UserProfileBean");
    		transaction.rollback();
    	} finally {
    	    transaction.commit();
    	}
    }
    
    /**
     * Returns the user profile page in its editable state.
     * 
     * @return link to the next page
     */
    public String editUserdata() {
	return null;
    }

    /**
     * Saves the edited user data and sets the user details page to its
     * not-editable state. In order to save the edited user, the database entry
     * of the user is updated with the new data and the <code>userProfil</code>
     * page is returned.
     * 
     * @return link to the next page
     */
    public void saveSettings() {
    	setEnums();
    	transaction = Connection.create();
    	transaction.start();
    	
    	try {
    		User checkUser = UserDAO.getUser(transaction, userID);
    		boolean nickTaken = UserDAO.nickTaken(transaction, user.getUsername());
    		boolean emailTaken = UserDAO.emailExists(transaction, user.getEmail());
    		
    		if (acceptUserInput(checkUser, nickTaken, emailTaken)) {
    			String pwHash = null;
    			String salt = null;
    			if (password != null) {
    				salt = PasswordHash.getSalt();
    				pwHash = PasswordHash.hash(password, salt);
    			}
    			UserDAO.updateUser(transaction, user, pwHash, salt);
    			transaction.commit();
    			readOnly = true;
    		} else {
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
    
    private void setEnums() {
    	if (salutation.equals("ms")) {
    		user.setSalutation(Salutation.MS);
    	} else {
    		user.setSalutation(Salutation.MR);
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
     * 
     * @param checkUser
     * @param nickTaken
     * @param emailTaken
     * @return
     * 
     * @author Patrick Cretu
     */
    private boolean acceptUserInput(User checkUser, boolean nickTaken, boolean emailTaken) {
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
    
    public String deleteUser() {
    	String goToPage = "/facelets/user/systemAdministrator/createUser.xhtml?faces-redirect=false";
    	transaction = Connection.create();
    	transaction.start();
    	
    	try {
    		UserDAO.delete(transaction, userID, true);
    		transaction.commit();
    		
    		goToPage = "/facelets/user/systemAdministrator/listUsers.xhtml?faces-redirect=true";
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
     * Initializes the profile page of the user with the details of the
     * user.
     */
    public void initialzeUserProfile() {
    }

    /**
     * Uploads a selected picture file from the local system to the server. The
     * picture needs to be a .jpg <br>
     */
    public void uploadProfilePic() {
    	if (image != null) {
    		transaction = Connection.create();
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
    
    public void deleteProfilePic() {
    	transaction = Connection.create();
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
     * Sets the <code>userRole</code> of the actual user to
     * <code>INACTIVE</code>.<br>
     * In order to do so the database entry of the user is also updated.<br>
     * The entry of the user is still in the database but the user cannot log in
     * anymore.
     * 
     * @return the new user status
     */
    public UserStatus setUserInactive() {
	return null;
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
    public void setUser(User userToSet) {
    }

    /**
     * Returns the value of the attribute <code>managedCourses</code>.
     * 
     * @return list of courses that are managed by a course leader
     */
    public List<Course> getManagedCourses() {
	return managedCourses;
    }

    /**
     * Sets the value of the attribute <code>managedCourses</code> that stores a
     * list of courses that are managed by a course leader.
     * 
     * @param managedCourses
     *            list of courses that are managed by a course leader
     */
    public void setManagedCourses(List<Course> managedCourses) {
    }

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
        pagination.setCurrentPageNumber(currentPage);
        transaction.start();
        try {
            managedCourses = UserDAO.getCoursesLeadedBy(transaction, userID, pagination);
            transaction.commit();
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error("Error occured during fetching data for pagination.");
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
    public void setSessionUser(SessionUserBean sessionUser) {
    	this.sessionUser = sessionUser;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	// TODO Auto-generated method stub

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

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
