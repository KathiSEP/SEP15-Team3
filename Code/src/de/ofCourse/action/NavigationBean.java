/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import de.ofCourse.model.Language;
import de.ofCourse.utilities.LanguageManager;

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
 * @author Ricky Strohmeier
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
     * Sets the language in which the whole site will appear.
     * 
     * @param language the language to be set.
     */
    public void setLanguage(String language) {
    	if (language.equals("en")) {
    		sessionUser.setLanguage(Language.EN);
    	} else if (language.equals("bay")) {
    	    sessionUser.setLanguage(Language.BAY);
    	} else {
    		sessionUser.setLanguage(Language.DE);
    	}
    }

    /**
     * Performs the logout action.<br>
     * By pressing the <code>logout</code> button, the user is logged out. That
     * means his userStatus is set to <code>ANONYMOUS</code> and he is
     * redirected to the welcome page.
     * 
     * @return the link to the welcome page
     */
    public String logout() {
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
}
