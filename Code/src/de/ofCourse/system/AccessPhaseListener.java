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
     * Represents the url to the index page
     */
    private static final String URL_INDEX = "/facelets/open/index.xhtml";
    
    /**
     * Represents the url to the login page
     */
    private static final String URL_AUTHENTICATE = "/facelets/open/authenticate.xhtml";
    
    /**
     * Represents the url to the course registration error page
     */
    private final static String URL_COURSE_REGISTRATION_ERROR = 
	    "/facelets/ErrorPages/CourseRegistrationException.xhtml";
    
    /**
     * Represents the url to the 404 error page
     */
    private final static String URL_404_ERROR = "/facelets/ErrorPages/404.xhtml";
    
    /**
     * Represents the url to the default error page
     */
    private final static String URL_DEFAULT_ERROR = "/facelets/ErrorPages/default.xhtml";
    
    /**
     * Represents the url to the imprint page
     */
    private final static String URL_IMPRINT = "/facelets/open/imprint.xhtml";
    
    /**
     * Represents the url to the agb page
     */
    private final static String URL_AGB = "/facelets/open/agb.xhtml";
    
    /**
     * Represents the url to the help page
     */
    private final static String URL_HELP = "/facelets/open/help.xhtml";
        
    /**
     * Represents the url to the page where you can search for courses
     */
    private final static String URL_SEARCH = "/facelets/open/courses/search.xhtml";
    
    /**
     * Represents the url to the course detail page
     */
    private final static String URL_COURSE_DETAIL = 
	    "/facelets/open/courses/courseDetail.xhtml";
    
    /**
     * Represents the url to the page where the courses of the user are listed
     */
    private final static String URL_MY_COURSES = 
	    "/facelets/user/registeredUser/myCourses.xhtml";
    
    /**
     * Represents the url to the profile page
     */
    private final static String URL_PROFILE = 
	    "/facelets/user/registeredUser/profile.xhtml";
    
    /**
     * Represents the url to the scheduler page
     */
    private final static String URL_SCHEDULER= 
	    "/facelets/user/registeredUser/scheduler.xhtml";
        
    /**
     * Represents the url to the profile page of a course leader
     */
    private final static String URL_LEADER_PROFILE = 
	    "/facelets/open/leaderProfile.xhtml";
    
    /**
     * Represents the url to the participants list page of a course
     */
    private final static String URL_PARTICIPANTS_LIST = 
	    "/facelets/user/registeredUser/listParticipants.xhtml";   
    
    /**
     * Represents the url to the profile page where a course leader 
     * create/edit/delete course units
     */
    private final static String URL_EDIT_COURSE_UNIT = 
	    "/facelets/user/courseLeader/editCourseUnit.xhtml";
    
    /**
     * Represents the url to the user activation page
     */
    private final static String URL_ACTIVATE_USERS = 
	    "/facelets/user/courseLeader/activateUsers.xhtml";
    
    /**
     * Represents the url to the administrator page
     */
    private final static String URL_ADMIN_MANAGEMENT = 
	    "/facelets/user/systemAdministrator/adminManagement.xhtml";
    
    /**
     * Represents the url to the page where a administrator can search for users
     */
    private final static String URL_SEARCH_USER = 
	    "/facelets/user/systemAdministrator/searchUser.xhtml";   
    
    /**
     * Represents the url to the page where a new user can be created
     */
    private final static String URL_CREATE_USER = 
	    "/facelets/user/systemAdministrator/createUser.xhtml";
    
    /**
     * Represents the url to the page where a new course can be created
     */
    private final static String URL_CREATE_COURSE = 
	    "/facelets/user/systemAdministrator/createCourse.xhtml";

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
	anonymousUsers = fillPageListForAnonymousUsers();
	registeredUsers = fillPageListForRegisteredUsers();
	courseLeaders = fillPageListForCourseLeaders();
	administrators = fillPageListForAdministrators();
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
	sessionUser = new SessionUserBean();
	String role ="";
	if (sessionMap.containsKey("userID")
		&& sessionMap.containsKey("userRole")) {

	    sessionUser.setUserID((int) sessionMap.get("userID"));
	    sessionUser.setUserRole((UserRole) sessionMap.get("userRole"));
	    role = sessionUser.getUserRole().toString();
	}
	

	if (!loginPage && !loggedIn) {
	    if (!this.isPermitted(fctx,"")) {
		this.redirect(ctx, URL_AUTHENTICATE);
	    }
	} else {
	    
	    switch (role) {
	    case "REGISTERED_USER":
		if (!isPermitted(fctx, "REGISTERED_USER")) {
		    redirect(ctx, URL_INDEX);
		}
		break;

	    case "COURSE_LEADER":
		if (!isPermitted(fctx, "COURSE_LEADER")) {
		    redirect(ctx, URL_INDEX);
		}
		break;

	    case "SYSTEM_ADMINISTRATOR":
		if (!isPermitted(fctx, "SYSTEM_ADMINISTRATOR")) {
		    redirect(ctx, URL_INDEX);
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
    private boolean isPermitted(FacesContext fctx, String role) {
	boolean isPermitted = false;

	if(fctx.getViewRoot()!= null){
	    switch (role) {
	
	    case "SYSTEM_ADMINISTRATOR":
		for (int i = 0; i < administrators.size() && !isPermitted; ++i) {
		    if (fctx.getViewRoot().getViewId()
			    .equals(administrators.get(i))) {
			isPermitted = true;
		    }
		}

	    case "COURSE_LEADER":
		for (int i = 0; i < courseLeaders.size() && !isPermitted; ++i) {
		    if (fctx.getViewRoot().getViewId().equals(courseLeaders.get(i))) {
			isPermitted = true;
		    }
		}

	    case "REGISTERED_USER":
		for (int i = 0; i < registeredUsers.size() && !isPermitted; ++i) {
		    if (fctx.getViewRoot().getViewId()
			    .equals(registeredUsers.get(i))) {
			isPermitted = true;
		    }
		}

	    default:
		for (int i = 0; i < anonymousUsers.size() && !isPermitted; ++i) {
		    if (fctx.getViewRoot().getViewId()
			.equals(anonymousUsers.get(i))|| isOwnProfile(fctx)) {
			isPermitted = true;
		    }
		}
	    }
	}
	return isPermitted;
    }
    
    /**
     * Checks whether the user requested his own profile.
     * 
     * @param fctx
     * 		the faces context
     * @return <code>true</code>, if the user is allowed to visit the requested profile.
     *         <cod>false</code>, otherwise
     */
    private boolean isOwnProfile(FacesContext fctx) {
	boolean ownProfile = false;
	if (fctx.getViewRoot().getViewId().endsWith("profile.xhtml")) {
	    int userID = 0;
	    
	    try {
		userID = Integer.parseInt(FacesContext.getCurrentInstance()
			.getExternalContext().getRequestParameterMap()
			.get("userID"));
	    } catch (NumberFormatException e) {
		userID = sessionUser.getUserID();
	    }
	    
	    if (sessionUser.getUserID() == userID) {
		ownProfile = true;
	    }
	}
	
	return ownProfile;
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
	
	listForAnonymousUsers.add(URL_COURSE_REGISTRATION_ERROR);
	listForAnonymousUsers.add(URL_404_ERROR);
	listForAnonymousUsers.add(URL_DEFAULT_ERROR);
	listForAnonymousUsers.add(URL_INDEX);
	listForAnonymousUsers.add(URL_AUTHENTICATE);
	listForAnonymousUsers.add(URL_IMPRINT);
	listForAnonymousUsers.add(URL_AGB);
	listForAnonymousUsers.add(URL_HELP);
	listForAnonymousUsers.add(URL_SEARCH);
	listForAnonymousUsers.add(URL_COURSE_DETAIL);
	listForAnonymousUsers.add(URL_LEADER_PROFILE);
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

	listForRegisteredUsers.add(URL_MY_COURSES);
	listForRegisteredUsers.add(URL_SCHEDULER);
	listForRegisteredUsers.add(URL_PARTICIPANTS_LIST);
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

	listForCourseLeaders.add(URL_EDIT_COURSE_UNIT);
	listForCourseLeaders.add(URL_ACTIVATE_USERS);
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

	listForAdministrators.add(URL_ADMIN_MANAGEMENT);
	listForAdministrators.add(URL_SEARCH_USER);
	listForAdministrators.add(URL_PROFILE);
	listForAdministrators.add(URL_CREATE_USER);
	listForAdministrators.add(URL_CREATE_COURSE);
	return listForAdministrators;
    }
}
