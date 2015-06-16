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
 * AccessPhaseListener implements the interface <code>PhaseListener</code>.
 * 
 * @author Tobias Fuchs
 *
 */
public class AccessPhaseListener implements PhaseListener {

    /**
     * Default serial version id
     */
    private static final long serialVersionUID = -5454041563674183939L;

    /**
     * List that contains the pages accessible for an anonymous user.
     */
    private List<String> anonymousUsers;

    /**
     * List that contains the pages accessible for a registered user.
     */
    private List<String> registeredUsers;

    /**
     * List that contains the pages accessible for course leader.
     */
    private List<String> courseLeaders;

    /**
     * List that contains the pages accessible for an system administrator.
     */
    private List<String> administrators;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * Constructor of the class <code>CheckPhase</code>.<br>
     * It fills the lists with the pages a user with a specific user role is
     * allowed to access.
     */
    public AccessPhaseListener() {
	this.anonymousUsers = this.fillPageListForAnonymousUsers();
	this.registeredUsers = this.fillPageListForRegisteredUsers();
	this.courseLeaders = this.fillPageListForCourseLeaders();
	this.administrators = this.fillPageListForAdministrators();

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
	UserRole role = sessionUser.getUserRole();

	if (!loginPage && !loggedIn) {
	    if (!this.isPermitted(fctx, null)) {
		this.redirect(ctx, "/facelets/open/authenticate.xhtml");
	    }
	} else {
	    switch (role) {
	    case REGISTERED_USER:
		if (!isPermitted(fctx, UserRole.REGISTERED_USER)) {
		    this.redirect(ctx, "/facelets/open/index.xhtml");
		}
		break;

	    case COURSE_LEADER:
		if (!isPermitted(fctx, UserRole.COURSE_LEADER)) {
		    this.redirect(ctx, "/facelets/open/index.xhtml");
		}
		break;

	    case SYSTEM_ADMINISTRATOR:
		if (!isPermitted(fctx, UserRole.SYSTEM_ADMINISTRATOR)) {
		    this.redirect(ctx, "/facelets/open/index.xhtml");
		}
		break;
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
     * Returns the id of the <code>Restore View</code> phase
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
    private void redirect(ExternalContext ctx, String page) {
	try {
	    ctx.redirect(ctx.getRequestContextPath() + page);
	} catch (IOException e) {
	    LogHandler.getInstance().error("Error during redirecting.");
	}
    }

    /**
     * Checks whether the user with its user role has the right to access the
     * requested page.
     * 
     * @param fctx
     *            the faces context
     * @param role
     *            the role of the user
     * @return <code>true</code>, if the user has the right to access the
     *         requested page<br>
     *         <code>false</code>, otherwise
     */
    private boolean isPermitted(FacesContext fctx, UserRole role) {
	boolean isPermitted = false;

	switch (role) {
	case SYSTEM_ADMINISTRATOR:
	    for (int i = 0; i < this.administrators.size() && !isPermitted; ++i) {
		if (fctx.getViewRoot().getViewId()
			.equals(administrators.get(i))) {
		    isPermitted = true;
		}
	    }

	case COURSE_LEADER:
	    for (int i = 0; i < this.courseLeaders.size() && !isPermitted; ++i) {
		if (fctx.getViewRoot().getViewId().equals(courseLeaders.get(i))) {
		    isPermitted = true;
		}
	    }

	case REGISTERED_USER:
	    for (int i = 0; i < this.registeredUsers.size() && !isPermitted; ++i) {
		if (fctx.getViewRoot().getViewId()
			.equals(registeredUsers.get(i))) {
		    isPermitted = true;
		}
	    }

	default:
	    for (int i = 0; i < this.anonymousUsers.size() && !isPermitted; ++i) {
		if (fctx.getViewRoot().getViewId()
			.equals(anonymousUsers.get(i))) {
		    isPermitted = true;
		}
	    }

	}
	return isPermitted;
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
	listForAnonymousUsers.add("/facelets/open/courses/courseDetail.xhtml");
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
		.add("/facelets/user/systemAdministrator/searchUser.xhtml");
	listForAdministrators
		.add("/facelets/user/systemAdministrator/createUser.xhtml");
	listForAdministrators
		.add("/facelets/user/systemAdministrator/createCourse.xhtml");
	return listForAdministrators;
    }

}
