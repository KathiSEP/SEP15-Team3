/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.PostConstruct;

import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.CourseUnitDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.CourseRegistrationException;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Displays the details of a course and its course units and provides the
 * functionality to edit the data of the course. In addition it manages the sign
 * up and sign off action of courses and course units.
 * <p>
 * 
 * A user can sign up for a course if he's not already signed in.<br>
 * He also can sign up for course units of the course. This is only possible if
 * the user is signed in in the respective course. The payment for a course unit
 * is done automatically by the system, that means that the price of the course
 * unit the user wants to sign in is automatically transfered from the users
 * account. That means that a user only can sign up for a course unit if he has
 * enough money on his account.<br>
 * If the user signs off from a course unit the paid money for the course unit
 * is transfered back to the users account automatically.
 * 
 * This class is Bean and controller of the facelet <code>coursDetails</code>.
 * 
 * @author Ricky Strohmeier
 *
 */
@ManagedBean
@ViewScoped
public class CourseDetailBean implements Pagination, Serializable {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 4941379076876916373L;

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the displayed or entered data of the course.
     */
    private Course course;

    /**
     * Stores whether a user registers for course news.
     */
    private boolean registeredForCourseNews;

    /**
     * Stores a list of course units that the user has selected
     */
    private List<CourseUnit> selectedCourseUnits;

    /**
     * Stores a list of leaders of the course
     */
    private List<User> leadersOfCourse;

    /**
     * Stores the course units that belong to the course
     */
    private List<CourseUnit> courseUnitsOfCourse;

    /**
     * Stores the leader to be add
     */
    private User leaderToAdd;

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

    private boolean editMode;
    private boolean isRegistered;
    private int courseID;
    private static int pageElements = 10;

    private int currentPage;

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
	return currentPage;
    }

    /**
     * @param currentPage
     *            the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
	this.currentPage = currentPage;
    }

    /**
     * Saves the edited course data and sets the course details page to its
     * not-editable state. In order to save the edited course, the database
     * entry of the course is updated with the new data and the
     * <code>courseDetails</code> page is returned.
     * 
     * @return link to the next page
     * @author Ricky Strohmeier
     */
    public String saveCourse() {
	if (getEditMode()) {
	    transaction = Connection.create();
	    try {
		transaction.start();
		CourseDAO.updateCourse(transaction, course);
		transaction.commit();
	    } catch (InvalidDBTransferException e) {
		transaction.rollback();
		LogHandler
			.getInstance()
			.error("Error occured in saveCourse method of CourseDetailBean");
	    }
	    setEditMode(false);
	}
	return "#";
    }

    /**
     * Initializes the course details page with the details of the course that
     * is to display.
     * 
     * @author Ricky Strohmeier
     */
    @PostConstruct
    private void init() {
	courseID = Integer.parseInt(FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get("courseID"));
	pagination = new PaginationData(pageElements, 0, "title", true);

	leaderToAdd = new User();
	transaction = Connection.create();
	try {
	    if (courseID > 0) {
		transaction.start();
		course = CourseDAO.getCourse(transaction, courseID);
		pagination.refreshNumberOfPages(CourseUnitDAO
			.getNumberOfCourseUnits(transaction, courseID));
		leadersOfCourse = CourseDAO.getLeaders(transaction, courseID);
		courseUnitsOfCourse = CourseUnitDAO.getCourseUnitsFromCourse(
			transaction, courseID, pagination);

		for (int i = 0; i < courseUnitsOfCourse.size(); i++) {
		    courseUnitsOfCourse.get(i).setUserIsParticipant(
			    UserDAO.userIsParticipantInCourseUnit(transaction,
				    sessionUser.getUserID(),
				    courseUnitsOfCourse.get(i)
					    .getCourseUnitID()));
		}
		isRegistered = UserDAO.userIsParticpant(transaction,
			sessionUser.getUserID(), courseID);
		transaction.commit();
	    }
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error occured in init method of CourseDetailBean");
	    courseID = 0;
	    isRegistered = false;
	    course = new Course();
	}
    }

    /**
     * Enables the edit mode of the facelet.
     * 
     * @return the same page
     * @author Ricky Strohmeier
     */
    public String enableEditMode() {
	setEditMode(true);
	return "#";
    }

    /**
     * Returns the value of the attribute <code>course</code>.
     * 
     * @return the course
     */
    public Course getCourse() {
	return course;
    }

    /**
     * Sets the value of the attribute <code>course</code>.
     * 
     * @param course
     *            the new course
     */
    public void setCourse(Course course) {
	this.course = course;
    }

    /**
     * Returns whether the user has registered for course news.
     * 
     * @return whether the user has registered for course news
     */
    public boolean getRegisteredForCourseNews() {
	return registeredForCourseNews;
    }

    /**
     * Set-method for the registered for course news attribute.
     * 
     * @param registeredForCourseNews
     *            true if registered, false if not
     * @author Ricky Strohmeier
     */
    public void setRegisteredForCourseNews(boolean registeredForCourseNews) {
	this.registeredForCourseNews = registeredForCourseNews;
    }

    /**
     * Signs a user up for a course and returns the updated page.<br>
     * Signing up for the course means that the user is set as participant of
     * the course and the course is added to the courses of the user. Also the
     * database entry of the course is updated.
     * 
     * <p>
     * If there goes something wrong during the sign up action, a
     * CourseRegistrationException is thrown.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occurs during the sign up process
     */
    public void signUpForCourse() throws CourseRegistrationException {
	// I have to start a transaction for database comunication
	Transaction trans = Connection.create();
	trans.start();

	try {
	    // First i fetch the number of Participants and the maximum amount a
	    // Course can handle
	    int numberOfParticipants = CourseDAO.getNumberOfParticipants(trans,
		    courseID);
	    Course courseToSignUp = CourseDAO.getCourse(trans, courseID);

	    // TODO Methode noch nicht implementiert feste zahl zum testen
	    // benutzt
	    if (courseToSignUp.getMaxUsers() > numberOfParticipants) {
		System.out.println(sessionUser.getUserID());

		// Add user to course_participant list on the database server
		CourseDAO.addUserToCourse(trans, sessionUser.getUserID(),
			courseID);
		if (registeredForCourseNews) {
		    CourseDAO.addUserToInformUser(trans,
			    sessionUser.getUserID(), courseID);
		}
		LogHandler.getInstance().debug(
			"User:" + sessionUser.getUserID()
				+ "Succesfull signed for course:" + courseID);
		trans.commit();
		isRegistered = true;
	    } else {
		// If the course is full we throw the
		// CourseRegistrationException
		trans.rollback();
		throw new CourseRegistrationException();
	    }
	} catch (InvalidDBTransferException e) {
	    trans.rollback();
	    // TODO Error Handling
	    LogHandler.getInstance().error(
		    "Error occured while User:" + sessionUser.getUserID()
			    + " signing up for course:" + courseID);
	    throw new CourseRegistrationException();
	}

    }

    /**
     * Signs a user off from a course and returns the updated page.<br>
     * Signing off from the course means that the user is removed as participant
     * of the course and the course is removed from the courses of the user.
     * Also the database entry of the course is updated.
     * 
     * <p>
     * If there goes something wrong during the sign off action a
     * CourseRegistrationException is thrown.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occours during the sign off process
     */
    public void signOffFromCourse() throws CourseRegistrationException {
	transaction = Connection.create();
	transaction.start();

	try {
	    User userWhoTryToSignOff = UserDAO.getUser(transaction,
		    sessionUser.getUserID());

	    // Fetching a List of all CourseUnits the User takes part in
	    List<CourseUnit> signedCourseUnits = CourseUnitDAO
		    .getCourseUnitsOf(transaction,
			    userWhoTryToSignOff.getUserID());

	    // Getting a List of Courses which only belongs to the Course and
	    // the User is signed Up
	    ArrayList<CourseUnit> courseUnitsToSignOff = findCourseUnitsOfThisCourse(signedCourseUnits);

	    // If the list is not empty the users gets signed Off from every
	    // CourseUnit which belongs to the Course and he is signedUp to
	    // Das geht schoener aber es is schon 4 Uhr in der Frueh
	    if (!courseUnitsToSignOff.isEmpty()) {
		float money = 0;
		for (int i = 0; i < courseUnitsToSignOff.size(); i++) {
		    CourseUnitDAO.removeUserFromCourseUnit(transaction,
			    sessionUser.getUserID(), courseUnitsToSignOff
				    .get(i).getCourseUnitID());
		    money += courseUnitsToSignOff.get(i).getPrice();

		    // die button auch wieder setzen
		    for (int x = 0; x < courseUnitsOfCourse.size(); x++) {
			if (courseUnitsOfCourse.get(x).getCourseUnitID() == courseUnitsToSignOff
				.get(i).getCourseUnitID()) {
			    courseUnitsOfCourse.get(x).setUserIsParticipant(
				    false);
			    break;
			}
		    }

		}

		float newAccountBalance = userWhoTryToSignOff
			.getAccountBalance() + money;
		UserDAO.updateAccountBalance(transaction,
			sessionUser.getUserID(), newAccountBalance);
		CourseDAO.removeUserFromCourse(transaction,
			sessionUser.getUserID(), courseID);

	    } else {
		CourseDAO.removeUserFromCourse(transaction,
			sessionUser.getUserID(), courseID);
	    }

	    // aus der inform liste loeschen falls drin
	    if (UserDAO.userWantsToBeInformed(transaction,
		    sessionUser.getUserID(), courseID)) {
		CourseDAO.removeUserToInformUser(transaction,
			sessionUser.getUserID(), courseID);
	    }

	    transaction.commit();
	    isRegistered = false;
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error occured while trying to sign off from course");
	    throw new CourseRegistrationException();
	}

    }

    /**
     * @param signedCourseUnits
     * @return
     */
    private ArrayList<CourseUnit> findCourseUnitsOfThisCourse(
	    List<CourseUnit> signedCourseUnits) {

	// This array stores the CourseUnitIDs which belong to the course the
	// user wants to sign off
	ArrayList<CourseUnit> courseUnitsOfThatCourse = new ArrayList<CourseUnit>();

	// If the user has not signed up to any courseUnits the Methode returns
	// a empty list
	if (!signedCourseUnits.isEmpty()) {

	    // If the user is in no courseUnit which belongs to the course he
	    // wants to leave the methode returns a empty list
	    for (int i = 0; i < signedCourseUnits.size(); i++) {
		if (signedCourseUnits.get(i).getCourseID() == courseID) {
		    courseUnitsOfThatCourse.add(signedCourseUnits.get(i));
		}
	    }
	    return courseUnitsOfThatCourse;
	} else {
	    return courseUnitsOfThatCourse;
	}

    }

    /**
     * Selects all avialible course units of a course at once.
     */
    public void selectAllCourseUnits() {
    }

    /**
     * Signs a user up for a course unit and returns the updated page.<br>
     * Signing up for the course unit means that the user is set as participant
     * of the course unit and the course unit is added to the course units of
     * the user. Also the database entry of the course unit is updated.
     * 
     * <p>
     * The user can only sign up for the course unit if it's not already full
     * and if the user has enough money on his account to pay for the unit.
     * During the signing up action the price of the course unit is
     * automatically removed from the account of the user. If there goes
     * something wrong during the sign up action, a CourseRegistrationException
     * is thrown and the paid price is transfered back to the account of the
     * user.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occurs during the sign up process
     */
    /**
     * @return
     * @throws CourseRegistrationException
     */
    public void signUpForCourseUnits() throws CourseRegistrationException {
	Transaction trans = Connection.create();
	trans.start();
	// TODO an rickys faclet anpassen
	if (UserDAO.userIsParticpant(trans, sessionUser.getUserID(), courseID)) {
	    int courseUnitID = Integer.parseInt(FacesContext
		    .getCurrentInstance().getExternalContext()
		    .getRequestParameterMap().get("courseUnitID"));
	    try {
		// Instanziere alle Models aus der Datenbank die gebraucht
		// werden
		CourseUnit courseUnitToSign = CourseUnitDAO.getCourseUnit(
			trans, courseUnitID);
		User userWhoTryToSignUp = UserDAO.getUser(trans,
			sessionUser.getUserID());
		int currentAmountOfParticipants = CourseUnitDAO
			.getNumberOfParticipants(trans, courseUnitID);

		if (courseUnitIsFull(courseUnitToSign,
			currentAmountOfParticipants)) {

		    // Methode berechnet neuen Kontostand
		    float newAccountBalance = signingUpForUser(
			    courseUnitToSign, userWhoTryToSignUp);

		    // Kontostand wird geupdatet und der User in die CourseUnit
		    // eingetragen
		    CourseUnitDAO.addUserToCourseUnit(trans,
			    sessionUser.getUserID(), courseUnitID);
		    UserDAO.updateAccountBalance(trans,
			    sessionUser.getUserID(), newAccountBalance);

		    // Instanziert den button
		    for (int i = 0; i < courseUnitsOfCourse.size(); i++) {
			if (courseUnitsOfCourse.get(i).getCourseUnitID() == courseUnitID) {
			    courseUnitsOfCourse.get(i).setUserIsParticipant(
				    true);
			    break;
			}
		    }

		    LogHandler.getInstance().debug(
			    "User succesfully added to CourseUnit");

		    trans.commit();
		} else {
		    LogHandler
			    .getInstance()
			    .debug("The courseUnit:"
				    + courseUnitID
				    + "has reached maximal amount of Particpants. User:"
				    + userWhoTryToSignUp.getUserID()
				    + "cant sign up");
		    trans.rollback();
		    throw new CourseRegistrationException();

		}

	    } catch (InvalidDBTransferException e) {
		LogHandler.getInstance()
			.error("Database Transfare not working");
		trans.rollback();
		throw new CourseRegistrationException();

	    } catch (CourseRegistrationException e) {
		trans.rollback();
		// TODO Faclet Messenge not enough money
		LogHandler.getInstance().error(
			"Not enough Money on the Account. User:"
				+ sessionUser.getUserID()
				+ "couldnt sign up for courseUnit:"
				+ courseUnitID);
	    }
	} else {
	    // TODO FacletMessege
	    trans.rollback();
	}

    }

    /**
     * Signs a user off from a course unit and returns the updated page.<br>
     * Signing off from the course unit means that the user is removed as
     * participant of the course unit and the course unit is removed from the
     * course units of the user. Also the database entry of the course unit is
     * updated.
     * 
     * <p>
     * The user can only sign off from the course unit if he is signed in.
     * During the signing off action the price of the course unit is
     * automatically transfered to the account of the user. If there goes
     * something wrong during the sign up action, a CourseRegistrationException
     * is thrown.
     * 
     * @return link to the page
     * 
     * @throws CourseRegistrationException
     *             if a exception occurs during the sign off process
     */
    public void signOffFromCourseUnits() throws CourseRegistrationException {
	Transaction trans = Connection.create();
	trans.start();
	// TODO noch angleichen zum FACLET
	int courseUnitID = Integer.parseInt(FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap()
		.get("courseUnitID"));
	try {

	    // Instanziere alle Models aus der Datenbank die gebraucht werden
	    CourseUnit courseUnitToSignOff = CourseUnitDAO.getCourseUnit(trans,
		    courseUnitID);
	    User userWhoTryToSignOff = UserDAO.getUser(trans,
		    sessionUser.getUserID());

	    // Kann hier auch noch abfragen ob User wirklich in Kurseinheit
	    signOffFromSpecificCourseUnit(trans, courseUnitToSignOff,
		    userWhoTryToSignOff);

	    // Instanziert noch den Damit der Button richtig angezeigt wird
	    for (int i = 0; i < courseUnitsOfCourse.size(); i++) {
		if (courseUnitsOfCourse.get(i).getCourseUnitID() == courseUnitID) {
		    courseUnitsOfCourse.get(i).setUserIsParticipant(false);
		    break;
		}
	    }
	    LogHandler.getInstance().debug(
		    "User sucessfully signed of from courseUnit");

	    trans.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().debug(
		    "User not signed off from courseUnit");
	    trans.rollback();
	}
    }

    /**
     * @param trans
     *
     * @param courseUnitToSignOff
     * @param userWhoTryToSignOff
     * @throws InvalidDBTransferException
     */
    private void signOffFromSpecificCourseUnit(Transaction trans,
	    CourseUnit courseUnitToSignOff, User userWhoTryToSignOff)
	    throws InvalidDBTransferException {
	CourseUnitDAO.removeUserFromCourseUnit(trans, sessionUser.getUserID(),
		courseUnitToSignOff.getCourseUnitID());
	float newAccountBalance = signingOffFromCourseUnit(courseUnitToSignOff,
		userWhoTryToSignOff);
	UserDAO.updateAccountBalance(trans, sessionUser.getUserID(),
		newAccountBalance);
    }

    /**
     * @author Sebastian
     * @param courseUnitToSign
     * @param currentAmountOfParticipants
     * @return
     */
    private boolean courseUnitIsFull(CourseUnit courseUnitToSign,
	    int currentAmountOfParticipants) {
	return courseUnitToSign.getMaxUsers() > currentAmountOfParticipants;
    }

    /**
     * Returns the value of the attribute <code>selectedCourseUnits</code>.
     * 
     * @return list of selected course units
     */
    public List<CourseUnit> getSelectedCourseUnits() {
	return selectedCourseUnits;
    }

    /**
     * Sets the value of the attribute <code>selectedCourseUnits</code>.
     * 
     * @param selectedCourseUnits
     *            the new list of selected course units
     */
    public void setSelectedCourseUnits(List<CourseUnit> selectedCourseUnits) {
	this.selectedCourseUnits = selectedCourseUnits;
    }

    /**
     * Returns the value of the attribute <code>leadersOfCourse</code>.
     * 
     * @return list of leaders
     */
    public List<User> getLeadersOfCourse() {
	return leadersOfCourse;
    }

    /**
     * Sets the value of the attribute <code>leadersOfCourse</code>.
     * 
     * @param leaders
     *            the new list of leaders
     */
    public void setLeadersOfCourse(List<User> leaders) {
	this.leadersOfCourse = leaders;
    }

    /**
     * Returns the value of the attribute <code>courseUnitsOfCourse</code>.
     * 
     * @return list of course units
     */
    public List<CourseUnit> getCourseUnitsOfCourse() {
	return courseUnitsOfCourse;
    }

    /**
     * Sets the value of the attribute <code>courseUnitsOfCourse</code>.
     * 
     * @param courseUnits
     *            the new list of course units
     */
    public void setCourseUnitsOfCourse(List<CourseUnit> courseUnits) {
	this.courseUnitsOfCourse = courseUnits;
    }

    /**
     * Redirect the user to a page where the participants of the course are
     * displayed
     * 
     * @return link to the next page
     */
    public String loadParticipantsPage() {
	return "/facelets/user/registeredUser/listParticipants.xhtml";
    }

    /**
     * Redirects the user to a page where he can edit a course unit.
     * 
     * @return link to the next page
     */
    public String loadEditCourseUnitPage() {
	return "/facelets/user/courseLeader/editCourseUnit.xhtml";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
	this.pagination.setCurrentPageNumber(this.currentPage);
	transaction.start();
	try {
	    courseUnitsOfCourse = CourseUnitDAO.getCourseUnitsFromCourse(
		    transaction, courseID, pagination);
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fetching data for pagination.");
	    this.transaction.rollback();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	// TODO Auto-generated method stub
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
    public void setSessionUser(SessionUserBean userSession) {
	this.sessionUser = userSession;
    }

    /**
     * Returns the edit mode flag.
     * 
     * @return true when in edit mode, false if not
     */
    public boolean getEditMode() {
	return editMode;
    }

    /**
     * Set-method for the edit mode flag.
     * 
     * @param editMode
     *            the boolean flag to be set
     */
    public void setEditMode(boolean editMode) {
	this.editMode = editMode;
    }

    /**
     * Returns the course's id.
     * 
     * @return the courseID
     */
    public int getCourseID() {
	return courseID;
    }

    /**
     * Set-method for the course's id.
     * 
     * @param courseID
     *            the courseID to set
     */
    public void setCourseID(int courseID) {
	this.courseID = courseID;
    }

    /**
     * @author Sebastian
     * @param courseUnitToSignOff
     * @param userWhoTryToSignOff
     * @return
     */
    private float signingOffFromCourseUnit(CourseUnit courseUnitToSignOff,
	    User userWhoTryToSignOff) {
	return userWhoTryToSignOff.getAccountBalance()
		+ courseUnitToSignOff.getPrice();
    }

    /**
     * @author Sebastian
     * @param courseUnitToSign
     * @param userWhoTryToSignUp
     * @return
     */
    private float signingUpForUser(CourseUnit courseUnitToSign,
	    User userWhoTryToSignUp) {
	if (userWhoTryToSignUp.getAccountBalance() >= courseUnitToSign
		.getPrice()) {
	    return userWhoTryToSignUp.getAccountBalance()
		    - courseUnitToSign.getPrice();
	} else {

	    // TODO FacletMessenge
	    LogHandler
		    .getInstance()
		    .debug("User:"
			    + userWhoTryToSignUp.getUserID()
			    + "has not enough money to take part in the course:"
			    + courseUnitToSign.getCourseUnitID());
	    throw new CourseRegistrationException();

	}

    }

    /**
     * Get method to see if the user is registered to this course.
     * 
     * @return the isRegistered
     * @author Ricky Strohmeier
     */
    public boolean getIsRegistered() {
	return isRegistered;
    }

    /**
     * Set method for the is registered boolean.
     * 
     * @param isRegistered
     *            the isRegistered to set
     * @author Ricky Strohmeier
     */
    public void setIsRegistered(boolean isRegistered) {
	this.isRegistered = isRegistered;
    }

    /**
     * Deletes the course from the system.
     * 
     * @return to the page search.xhtml
     * 
     * @author Katharina Hölzl
     */
    public String deleteCourse() {

	// Create a new transaction object for the database connection.
	this.transaction = Connection.create();
	transaction.start();

	try {

	    if (CourseDAO.deleteCourse(this.transaction,
		    this.course.getCourseID()) == true) {
		FacesMessageCreator.createFacesMessage(null,
			"Kurs wurde erfolgreich gelöscht!");
		this.transaction.commit();
		// Forwarding to the page search, because the delete was
		// successful.
		return "/facelets/open/courses/search.xhtml?faces-redirect=true";
	    } else {
		FacesMessageCreator.createFacesMessage(null,
			"Löschen des Kurses fehlgeschlagen!");
		this.transaction.rollback();
		return "/facelets/open/courses/courseDetail.xhtml?faces-redirect=false";
	    }

	} catch (InvalidDBTransferException e) {
	    this.transaction.rollback();
	}

	return "/facelets/open/courses/courseDetail.xhtml?faces-redirect=false";
    }

    /**
     * Adds a course leader to a course.
     * 
     * @author Katharina Hölzl
     */
    public void addCourseLeader() {

	// Create a new transaction object for the database connection.
	this.transaction = Connection.create();
	transaction.start();
	try {
	    if (CourseDAO.addLeaderToCourse(this.transaction,
		    this.leaderToAdd.getUserID(), this.course.getCourseID())) {
		FacesMessageCreator.createFacesMessage(null,
			"Der Kursleiter wurde erfolgreich hinzugefügt!");
	    }
	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    FacesMessageCreator.createFacesMessage(null,
		    "Hinzufügen des Kursleiters fehlgeschlagen!");
	    this.transaction.rollback();
	}

    }

    /**
     * Returns the value of the attribute <code>leaderToAdd</code>.
     * 
     * @return the leader to be add
     */
    public User getLeaderToAdd() {
	return leaderToAdd;
    }

    /**
     * Sets the value of the attribute <code>leaderToAdd</code>.
     * 
     * @param leaderToAdd
     *            the leader to be add
     */
    public void setLeaderToAdd(User leaderToAdd) {
	this.leaderToAdd = leaderToAdd;
    }

    /**
     * removes the target course leaders
     * 
     * @author Katharina Hölzl
     */
    public void removeCourseLeaders() {
	// Create a new transaction object for the database connection.
	this.transaction = Connection.create();
	transaction.start();

	String delLeaderString = FacesContext
		.getCurrentInstance().getExternalContext().getRequestParameterMap().get("leaderID");
System.out.println(delLeaderString);
	int delLeaderID;
	try {
	    delLeaderID = Integer.parseInt(delLeaderString);
	    if (CourseDAO.removeLeaderFromCourse(this.transaction, delLeaderID,
		    this.course.getCourseID())) {
		FacesMessageCreator.createFacesMessage(null,
			"Der Kursleiter wurde erfolgreich hinzugefügt!");
		this.transaction.commit();
	    } else {
		FacesMessageCreator.createFacesMessage(null,
			"Hinzufügen des Kursleiters fehlgeschlagen!");
		this.transaction.rollback();
	    }
	} catch (NumberFormatException e) {
	    // ID is no number
	    this.transaction.rollback();
	    FacesMessageCreator.createFacesMessage(null,
		    "Der Kursleiter konnte nicht gelöscht werden, da die "
			    + "übergebene ID keine positive Zahl ist!");
	}
    }

}
