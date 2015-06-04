/**
 * This package represents system functionality. 
 */
package de.ofCourse.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import de.ofCourse.action.SessionUserBean;
import de.ofCourse.model.UserRole;

/**
 * Represents a PhaseListener that is responsible for blocking unauthorized
 * access to a certain page.<br>
 * It prevents users from getting to a certain page without having the required
 * rights.
 * 
 * <p>
 * In case a user requests a page without having the necessary rights, he is
 * redirected to the start page of the system.<br>
 * CheckPhase implements the interface <code>PhaseListener</code>.
 * 
 * @author Tobias Fuchs
 *
 */
public class CheckPhase implements PhaseListener {

    /**
     * Default serial version id
     */
    private static final long serialVersionUID = -5454041563674183939L;

    /**
     * List that contains the pages accessible for an anonymous user.
     */
    private List<String> pageListForAnonymousUsers;

    /**
     * List that contains the pages accessible for a registered user.
     */
    private List<String> pageListForRegisteredUsers;

    /**
     * List that contains the pages accessible for course leader.
     */
    private List<String> pageListForCourseLeaders;

    /**
     * List that contains the pages accessible for an system administrator.
     */
    private List<String> pageListForAdministrators;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    private Transaction transaction;

    /**
     * Constructor of the class <code>CheckPhase</code>.<br>
     * It fills the lists with the pages a user with a specific user role is
     * allowed to access.
     */
    public CheckPhase() {
	this.pageListForAnonymousUsers = this.fillPageListForAnonymousUsers();
	this.pageListForRegisteredUsers = this.fillPageListForRegisteredUsers();
	this.pageListForCourseLeaders = this.fillPageListForCourseLeaders();
	this.pageListForAdministrators = this.fillPageListForAdministrators();

    }

    /**
     * Returns a list that contains the pages a user who is not logged in is
     * allowed to access.
     * 
     * @return list with the pages for the anonymous user
     */
    private List<String> fillPageListForAnonymousUsers() {
	ArrayList<String> listForAnonymousUsers = new ArrayList<String>();

	listForAnonymousUsers.add("/facelets/open/errorPage.xhtml");
	listForAnonymousUsers.add("/facelets/open/index.xhtml");
	listForAnonymousUsers.add("/facelets/open/authenticate.xhtml");
	listForAnonymousUsers.add("/facelets/open/imprint.xhtml");
	listForAnonymousUsers.add("/facelets/open/agb.xhtml");
	listForAnonymousUsers.add("/facelets/open/help.xhtml");
	listForAnonymousUsers.add("/facelets/open/courses/search.xhtml");
	listForAnonymousUsers.add("/facelets/open/courses/courseDetails.xhtml");
	return listForAnonymousUsers;
    }

    /**
     * Returns a list that contains the pages a user who is registered in the
     * system is allowed to access.
     * 
     * @return list with the pages for the registered user
     */
    private List<String> fillPageListForRegisteredUsers() {
	ArrayList<String> listForRegisteredUsers = new ArrayList<String>();

	listForRegisteredUsers
		.add("/facelets/user/registeredUser/myCourses.xhtml");
	listForRegisteredUsers
		.add("/facelets/user/registeredUser/profile.xhtml");
	listForRegisteredUsers
		.add("/facelets/user/registeredUser/scheduler.xhtml");
	listForRegisteredUsers
		.add("/facelets/user/registeredUser/leaderProfile.xhtml");
	listForRegisteredUsers
		.add("/facelets/user/registeredUser/listParticipants.xhtml");
	return listForRegisteredUsers;
    }

    /**
     * Returns a list that contains the pages a course leader is allowed to
     * access.
     * 
     * @return list with the pages for a course leader
     */
    private List<String> fillPageListForCourseLeaders() {
	ArrayList<String> listForCourseLeaders = new ArrayList<String>();

	listForCourseLeaders
		.add("/facelets/user/courseLeader/editCourseUnit.xhtml");
	listForCourseLeaders
		.add("/facelets/user/courseLeader/activateUsers.xhtml");
	return listForCourseLeaders;
    }

    /**
     * Returns a list that contains the pages a administrator is allowed to
     * access.
     * 
     * @return list with the pages for an administrator
     */
    private List<String> fillPageListForAdministrators() {
	ArrayList<String> listForAdministrators = new ArrayList<String>();

	listForAdministrators
		.add("/facelets/user/systemAdministrator/adminManagement.xhtml");
	listForAdministrators
		.add("/facelets/user/systemAdministrator/listUsers.xhtml");
	listForAdministrators
		.add("/facelets/user/systemAdministrator/createUser.xhtml");
	listForAdministrators
		.add("/facelets/user/systemAdministrator/createCourse.xhtml");
	return listForAdministrators;
    }

    /**
     * Handles the check whether the user has the required rights to get to the
     * requested page.<br>
     * If the user has this rights, he is directed to the requested page.<br>
     * Otherwise he is redirected to the start page of the system.
     * 
     * @param event
     *            the phase events
     */
    @Override
    public void afterPhase(PhaseEvent event) {

	// Fetches the FacesContext parameters
	FacesContext fctx = event.getFacesContext();
	ExternalContext ctx = fctx.getExternalContext();
	Map<String, Object> sessionMap = ctx.getSessionMap();

	// Is the user on the login page?
	boolean loginPage = false;
	UIViewRoot viewRoot = fctx.getViewRoot();
	if (viewRoot != null) {
	    loginPage = fctx.getViewRoot().getViewId()
		    .endsWith("authenticate.xhtml");
	}

	// Is the user already authenticated?
	boolean loggedIn = sessionMap.containsKey("loggedin");

	// Fetch data from session
	this.sessionUser = new SessionUserBean();
	if (sessionMap.containsKey("userID")
		&& sessionMap.containsKey("userRole")) {

	    sessionUser.setUserID((int) sessionMap.get("userID"));
	    sessionUser.setUserRole((UserRole) sessionMap.get("userRole"));
	}


	
	if (!loginPage && !loggedIn) {
	    if (!this.isPermittedPageAnonymousUser(fctx)) {
		// Redirect to login page
		this.redirectTo(ctx, "/facelets/open/authenticate.xhtml");
	    }
	} else {
	    if (sessionUser.getUserRole() == UserRole.REGISTERED_USER) {
		if (!isPermittedPageRegisteredUser(fctx)) {
		    this.redirectTo(ctx, "/facelets/open/index.xhtml");
		}
	    } else if (sessionUser.getUserRole() == UserRole.COURSE_LEADER) {
		if (!isPermittedPageCourseLeader(fctx)) {
		    this.redirectTo(ctx, "/facelets/open/index.xhtml");
		}

	    } else if (sessionUser.getUserRole() == UserRole.SYSTEM_ADMINISTRATOR) {
		if (!isPermittedPageAdministrator(fctx)) {
		    this.redirectTo(ctx, "/facelets/open/index.xhtml");
		}
	    }
	}
    }

    /**
     * Handle a notification that the processing for a particular phase of the
     * request processing life cycle is about to begin.
     * 
     * @param event
     *            the phase events
     */
    @Override
    public void beforePhase(PhaseEvent event) {

    }

    /**
     * Returns the identifier of the request processing phase during which this
     * listener is interested in processing PhaseEvent events.
     * 
     * @return the id of the <code>RESTORE_VIEW</code> phase
     */
    @Override
    public PhaseId getPhaseId() {
	return PhaseId.RESTORE_VIEW;
    }

    /**
     * Redirects to the page determined by the String <page>.
     * 
     * @param ctx
     *            the external context
     * @param page
     *            the path of the page to redirect to
     */
    private void redirectTo(ExternalContext ctx, String page) {
	try {
	    ctx.redirect(ctx.getRequestContextPath() + page);
	} catch (IOException e) {
	    // TODO: Logging message
	}
    }

    /**
     * Checks whether the page determined by the entered url is accessible for
     * anonymous users.
     * 
     * @param fctx
     *            the faces context
     * @return <code>true</code>, if the page is accessible for anonymous users<br>
     *         <code>false</code>, otherwise
     */
    private boolean isPermittedPageAnonymousUser(FacesContext fctx) {
	for (String page : pageListForAnonymousUsers) {
	    if (fctx.getViewRoot().getViewId().equals(page)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Checks whether the page determined by the entered url is accessible for
     * registered users.
     * 
     * @param fctx
     *            the faces context
     * @return <code>true</code>, if the page is accessible for registered users<br>
     *         <code>false</code>, otherwise
     */
    private boolean isPermittedPageRegisteredUser(FacesContext fctx) {
	if (this.isPermittedPageAnonymousUser(fctx)) {
	    return true;
	} else {
	    for (String page : pageListForRegisteredUsers) {
		if (fctx.getViewRoot().getViewId().equals(page)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Checks whether the page determined by the entered url is accessible for
     * course leader.
     * 
     * @param fctx
     *            the faces context
     * @return <code>true</code>, if the page is accessible for course leaders<br>
     *         <code>false</code>, otherwise
     */
    private boolean isPermittedPageCourseLeader(FacesContext fctx) {
	if (this.isPermittedPageRegisteredUser(fctx)) {
	    return true;
	} else {
	    for (String page : pageListForCourseLeaders) {
		if (fctx.getViewRoot().getViewId().equals(page)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Checks whether the page determined by the entered url is accessible for
     * administrator.
     * 
     * @param fctx
     *            the faces context
     * @return <code>true</code>, if the page is accessible for administrator<br>
     *         <code>false</code>, otherwise
     */
    private boolean isPermittedPageAdministrator(FacesContext fctx) {
	if (this.isPermittedPageCourseLeader(fctx)) {
	    return true;
	} else {
	    for (String page : pageListForAdministrators) {
		if (fctx.getViewRoot().getViewId().equals(page)) {
		    return true;
		}
	    }
	}
	return false;
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
