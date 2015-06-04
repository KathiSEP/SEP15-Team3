/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;

import de.ofCourse.model.UserRole;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Transaction;

/**
 * Provides the services of directing to the login page, selecting the displayed
 * language and to perform the logout action.
 * <p>
 * 
 * Logging out of the system means that the user session is invalidated and the
 * user has to login again if he wants to access the system. The user can log
 * out from every page of the system. Only requirement is that the user is
 * logged in.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>navigation</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@RequestScoped
public class NavigationBean {

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    private boolean isAdmin;

    /**
     * Performs the logout action.<br> By pressing the <code>logout</code>
     * button, the user is logged out. That means his userStatus is set to
     * <code>ANONYMOUS</code> and he is redirected to the welcome page.
     * 
     * @return the link to the welcome page
     */
    public String logout() {
    	sessionUser = null;
    	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("loggedin");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/facelets/open/index.xhtml?faces-redirect=true";
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

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
