/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import de.ofCourse.Database.dao.CourseUnitDAO;
import de.ofCourse.Database.dao.CycleDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.CourseRegistrationException;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Cycle;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides functionality for course leaders to create, edit and delete course
 * units and to manage the users of the course unit, so to say, to add and to
 * remove users from course unit.
 * <p>
 * 
 * Only course leaders and administrators are able to use this functionalities.<br>
 * Especially only they have the rights to manage the users of a course unit. In
 * order to do so they have the possibility to add a new user to a course unit
 * by entering the data of the user. This user need not to pay for this course
 * unit<br>
 * For deleting a users from a course unit the course leader has to selected the
 * user to delete. He has the possibility to remove more than one user from a
 * course at once.
 * <p>
 * Furthermore this class offers the functionality to create and edit course
 * units that take place regularly at once.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>editCourseUnit</code>.
 * 
 * @author Tobias Fuchs
 *
 */
@ManagedBean
@ViewScoped
public class CourseUnitManagementBean implements Pagination, Serializable {

    /**
     * Serial version id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Stores the number of elements that are displayed by pagination at once
     */
    private static final int elementsPerPage = 10;

    private static final int informNobody = 0;

    private static final int informParticipantsOfUnit = 1;

    private int selectedToInform;

    /**
     * Constant that represents the days of a day
     */
    private static final int day = 1;

    /**
     * Constant that represents the number of days of a week
     */
    private static final int week = 7;

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores an user that is to be added to the course unit.
     */
    private User userToAdd;

    /**
     * Stores a list of users that participate a course unit.
     */
    private DataModel<User> participants;

    /**
     * Stores the entered start time of the course unit
     */
    private Date start;

    /**
     * Stores the entered endtime of the course unit
     */
    private Date end;

    /**
     * Stores the entered endtime of the course unit
     */
    private Date date;

    /**
     * Stores a list of users that are to be removed from the course unit.
     */
    private List<User> usersToDelete;

    /**
     * Stores whether a course unit takes place regularly.
     */
    private boolean regularCourseUnit;

    /**
     * Stores the entered or displayed data of the course unit.
     */
    private CourseUnit courseUnit;

    /**
     * Stores whether the page is rendered in edit mode
     */
    private boolean editMode;

    /**
     * Stores whether all course units of the corresponding cycle are to delete.
     */
    private boolean deleteAll;

    /**
     * Stores whether all course units of the corresponding cycle are to edit.
     */
    private boolean editAll;

    private int courseUnitID = 0;

    private int courseID;

    /**
     * This attribute represents a pagination object. It stores all the
     * information that is necessary for pagination, e.g. the number of elements
     * per page.
     */
    private PaginationData pagination;

    public MailBean getMailBean() {
	return mailBean;
    }

    public void setMailBean(MailBean mailBean) {
	this.mailBean = mailBean;
    }

    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    @PostConstruct
    private void init() {
	pagination = new PaginationData(elementsPerPage, 0, "title", true);
	this.participants = new ListDataModel<User>();
	this.usersToDelete = new ArrayList<User>();
	courseUnit = new CourseUnit();
	courseUnit.setAddress(new Address());
	courseUnit.setCourseAdmin(new User());
	courseUnit.setCycle(new Cycle());
	this.date = new Date();
	this.start = new Date();
	this.end = new Date();
	this.end.setTime((this.start.getTime() + 7200000L));

	// ///////////////////////////////////////////////
	// Mit Werten von CourseDetails füllen
	// ////////////////////////////////////////////////
	/*
	 * String fetchedMode = FacesContext.getCurrentInstance()
	 * .getExternalContext().getRequestParameterMap().get("editMode"); if
	 * (fetchedMode!=null && fetchedMode.toLowerCase().equals("true")) {
	 * editMode = true; } else { editMode = false; }
	 * 
	 * courseID = Integer.parseInt(FacesContext.getCurrentInstance()
	 * .getExternalContext().getRequestParameterMap().get("courseID")); if
	 * (editMode) { courseUnitID =
	 * Integer.parseInt(FacesContext.getCurrentInstance()
	 * .getExternalContext().getRequestParameterMap() .get("courseUnitID"));
	 * } else { courseUnitID = 0; }
	 */
	// ///////////////////////////////////////////////////////
	this.courseID = 10000;
	this.courseUnitID = 10000;
	this.editMode = false;

	transaction = Connection.create();
	transaction.start();

	// Initializes the pagination and if in edit mode the displayed course
	// unit
	try {

	    // this.courseUnit = CourseUnitDAO.getCourseUnit(transaction,
	    // courseUnitID);
	    this.pagination.actualizeNumberOfPages(CourseUnitDAO
		    .getNumberOfParticipants(transaction, courseUnitID));
	    this.participants.setWrappedData(CourseUnitDAO
		    .getParticipiantsOfCourseUnit(transaction, pagination,
			    courseUnitID, false));

	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during updating the"
			    + " page with elements from database.");
	    this.transaction.rollback();
	}

	// ///////////////////////////////////////////////////
	courseUnit.getAddress().setHouseNumber(6);
	courseUnit.getAddress().setStreet("Roehrn");
	courseUnit.getAddress().setCity("Ortenburg");
	courseUnit.getAddress().setZipCode(94496);
	courseUnit.setDescription("Das ist eine Beschreibung");
	courseUnit.getCourseAdmin().setUsername("Sepp");
	// ///////////////////////////////////////

    }

    /**
     * Creates a new course unit with the entered data and returns the link to
     * the updated course details page. That means that the method creates a new
     * database entry for the course unit and stores it in the database.<br>
     * By selecting that the course unit takes place regularly and entering
     * information relating to the cycle of the course units, it is possible to
     * create more regular course units at once.
     * 
     * @return link to the courseDetails page
     */
    public String createCourseUnit() {
	transaction.start();
	try {

	    calculateStartAndEndTime(this.courseUnit);

	    if (this.regularCourseUnit) {

		courseUnit.getCycle().setCycleID(
			CycleDAO.createCycle(transaction, courseID,
				courseUnit.getCycle()));

		Date actualStartDate = this.courseUnit.getStartime();
		Date actualEndDate = this.courseUnit.getEndtime();

		Date nextStartDate = null;
		Date nextEndDate = null;
		for (int i = 0; i < this.courseUnit.getCycle()
			.getNumberOfUnits(); ++i) {

		    if (this.courseUnit.getCycle().getTurnus() == day) {
			nextStartDate = this.calculateDate(actualStartDate, i
				* day);
			nextEndDate = this
				.calculateDate(actualEndDate, i * day);
		    } else if (this.courseUnit.getCycle().getTurnus() == week) {
			nextStartDate = this.calculateDate(actualStartDate, i
				* week);
			nextEndDate = this.calculateDate(actualEndDate, i
				* week);
		    }
		    this.courseUnit.setStartime(new Date(nextStartDate
			    .getTime()));
		    this.courseUnit.setEndtime(new Date(nextEndDate.getTime()));
		    CourseUnitDAO.createCourseUnit(transaction,
			    this.courseUnit, this.courseID, true);
		}
	    } else {
		this.courseUnit.getCycle().setCycleID(-1);
		CourseUnitDAO.createCourseUnit(transaction, courseUnit,
			courseID, false);
	    }
	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance()
		    .error("Error occured during creating the"
			    + " a new course unit.");
	    this.transaction.rollback();
	}
	return "/facelets/open/courses/courseDetail.xhtml?faces-redirect=true";
    }

    /**
     * Realizes the editing of a course unit that takes place regularly.<br>
     * If <code>editAllIfRegular</code> is <code>true</code>, all course units
     * in the cycle of this course unit are edited.<br>
     * If <code>editAllIfRegular</code> is <code>false</code>, only this course
     * unit is edited.<br>
     * Editing the course unit means that the database entry(ies) of the course
     * unit(s) is updated.<br>
     * After the edit, the participants receive a message to inform about the
     * changes.
     * 
     * @return the link to this page
     */
    public String saveCourseUnit() {
	CourseUnit tempUnit = null;
	long startdate_initial = courseUnit.getStartime().getTime();
	long enddate_initial = courseUnit.getEndtime().getTime();

	// Update local course unit attribute with entered data
	this.calculateStartAndEndTime(courseUnit);
	long startdate_new = courseUnit.getStartime().getTime();
	long enddate_new = courseUnit.getEndtime().getTime();

	long difference_start_time = startdate_new - startdate_initial;
	long difference_end_time = enddate_new - enddate_initial;

	transaction.start();
	// TODO: Not YET Tested
	try {
	    if (editAll && this.courseUnit.getCycle() != null) {
		ArrayList<Integer> idsToEdit = (ArrayList<Integer>) CourseUnitDAO
			.getIdsCourseUnitsOfCycle(transaction,
				courseUnit.getCourseUnitID());
		for (int id : idsToEdit) {
		    // Fetch course unit in cycle
		    tempUnit = CourseUnitDAO.getCourseUnit(transaction, id);

		    // Add calculated time differences
		    tempUnit.setStartime(new Date(tempUnit.getStartime()
			    .getTime() + difference_start_time));
		    tempUnit.setEndtime(new Date(tempUnit.getEndtime()
			    .getTime() + difference_end_time));
		    
		    // Update tempUnit with other entered values
		    tempUnit.setTitle(courseUnit.getTitle());
		    tempUnit.setMinUsers(courseUnit.getMinUsers());
		    tempUnit.setMaxUsers(courseUnit.getMaxUsers());
		    
		    
		    
		    
		    // update tempUnit
		    CourseUnitDAO.updateCourseUnit(transaction, tempUnit);

		}
	    } else {
		// New dates are already calculated
		CourseUnitDAO.updateCourseUnit(transaction, getCourseUnit());
	    }
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    e.printStackTrace();
	    LogHandler.getInstance().error(
		    "Error occured during deleting" + " a course unit.");
	}

	System.out.println("Save Course Unit");
	return "/facelets/open/courses/courseDetail.xhtml?faces-redirect=true";
    }

    private void editSingleUnit() {
    }

    /**
     * Deletes the course unit from the course and returns the link to the
     * course details page.<br>
     * That means that the method deletes the course unit from the database. The
     * money that the participants have paid for the course units is
     * automatically transfered back to their accounts.
     * 
     * @return link to the course details page
     */
    public String deleteCourseUnit() {
	transaction.start();
	// TODO: Not YET Tested
	try {
	    if (this.courseUnit.getCycle() != null && deleteAll) {
		ArrayList<Integer> idsToDelete = (ArrayList<Integer>) CourseUnitDAO
			.getIdsCourseUnitsOfCycle(transaction,
				courseUnit.getCourseUnitID());
		for (int id : idsToDelete) {
		    this.deleteSingleUnit(transaction, id);
		}
	    } else {
		this.deleteSingleUnit(transaction, courseUnit.getCourseUnitID());
	    }
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    e.printStackTrace();
	    LogHandler.getInstance().error(
		    "Error occured during deleting" + " a course unit.");
	}
	System.out.println("Delete Course Unit");
	return "/facelets/open/courses/courseDetail.xhtml?faces-redirect=true";
    }

    private void deleteSingleUnit(Transaction trans, int unitId) {
	try {
	    ArrayList<User> participants = (ArrayList<User>) CourseUnitDAO
		    .getParticipiantsOfCourseUnit(transaction, pagination,
			    unitId, true);
	    for (User user : participants) {
		CourseUnitDAO.removeUserFromCourseUnit(transaction,
			user.getUserID(), unitId);
		float newAccountBalance = calculateNewAccountBalance(
			(CourseUnitDAO.getPriceOfUnit(transaction, unitId)),
			user, false);
		UserDAO.updateAccountBalance(transaction, user.getUserID(),
			newAccountBalance);
	    }
	    CourseUnitDAO.deleteCourseUnit(transaction, unitId);
	    sendMailToSelected(transaction, participants);
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error during deleting a single course unit.");
	    throw new InvalidDBTransferException();
	}
    }

    private void sendMailToSelected(Transaction trans,
	    ArrayList<User> participants) {
	int recipientsGroup = this.getSelectedToInform();
	ArrayList<String> recipients = new ArrayList<String>();
	User tempUser = new User();
	for (User user : participants) {
	    if (recipientsGroup == informParticipantsOfUnit) {
		if (CourseUnitDAO.userWantsToBeInformed(transaction,
			user.getUserID())) {
		    tempUser = UserDAO.getUser(trans, user.getUserID());
		    recipients.add(tempUser.getEmail());
		    // TODO: SEND MAIL
		}
	    }
	}
    }

    /**
     * @param courseUnit
     * @param user
     * @return
     */
    private float calculateNewAccountBalance(float price, User user,
	    boolean signUp) {
	if (!signUp) {
	    if (user.getAccountBalance() >= price) {
		return user.getAccountBalance() - price;
	    } else {
		LogHandler.getInstance().debug(
			"Not enough money to sign in course unit");
		throw new CourseRegistrationException();
	    }
	} else {
	    return user.getAccountBalance() + price;
	}
    }

    /**
     * Adds the entered user to the course unit. That means the user is added to
     * the participants list and the database entry of the course unit is
     * updated. In this case the user need not to pay for the course unit.
     */
    public void addUserToCourseUnit() {
	transaction.start();
	try {
	    float newAccountBalance = this.calculateNewAccountBalance(
		    courseUnit.getPrice(), this.userToAdd, true);
	    CourseUnitDAO.addUserToCourseUnit(transaction,
		    userToAdd.getUserID(), courseUnit.getCourseUnitID());
	    UserDAO.updateAccountBalance(transaction, userToAdd.getUserID(),
		    newAccountBalance);
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error during adding user manually to course unit.");
	} catch (CourseRegistrationException e) {
	    LogHandler
		    .getInstance()
		    .error("Error during adding user manually to course unit. "
			    + "User has not enough money to participate the course unit");
	}
    }

    /**
     * Returns the value of the attribute <code>userToAdd</code>.
     * 
     * @return the user to add to the course unit
     */
    public User getUserToAdd() {
	return userToAdd;
    }

    /**
     * Sets the value of the attribute <code>usersToAdd</code>.
     * 
     * @param userToAdd
     *            the new user that is to be added to the course unit
     */
    public void setUserToAdd(User userToAdd) {
	this.userToAdd = userToAdd;
    }

    /**
     * Deletes the selected users from the course unit and transfer the paid
     * price for the course unit automatically back to the accounts of the
     * participants.<br>
     * Deleting means the users are removed from the participants list and the
     * database entry of the course unit is updated.
     */
    public void deleteUsersFromCourseUnit() {
	@SuppressWarnings("unchecked")
	List<User> items = (List<User>) this.participants.getWrappedData();
	usersToDelete = new ArrayList<User>();
	// Fetches the selected user that are to delete
	for (User item : items) {
	    if (item.isSelected()
		    && !(this.usersToDelete.contains(item.getUserID()))) {
		this.usersToDelete.add(item);
	    }
	}

	// Delete the users from course unit
	transaction.start();
	try {
	    for (int i = 0; i < usersToDelete.size(); ++i) {
		CourseUnitDAO.removeUserFromCourseUnit(transaction,
			sessionUser.getUserID(), courseUnit.getCourseUnitID());
		float newAccountBalance = calculateNewAccountBalance(
			courseUnit.getPrice(), usersToDelete.get(i), false);
		UserDAO.updateAccountBalance(transaction, usersToDelete.get(i)
			.getUserID(), newAccountBalance);
	    }

	    // Updates the shown list with the actual data
	    this.participants.setWrappedData(CourseUnitDAO
		    .getParticipiantsOfCourseUnit(transaction, pagination,
			    courseUnitID, false));
	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during deleting users"
			    + " form course unit or while fetching"
			    + " participants to display.");
	    this.transaction.rollback();
	}
    }

    /**
     * Returns the value of the attribute <code>usersToDelete</code> that stores
     * the users that are selected and shall be deleted from the course unit.
     * 
     * @return list of users to delete from the course unit
     */
    public List<User> getUsersToDelete() {
	return usersToDelete;
    }

    /**
     * Sets the value of the attribute <code>usersToDelete</code> that stores
     * the users that are selected and shall be deleted from the course unit.
     * 
     * @param usersToDelete
     *            new list of users to delete from the course unit
     */
    public void setUsersToDelete(List<User> usersToDelete) {
	this.usersToDelete = usersToDelete;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    /**
     * @return the regularCourseUnit
     */
    public boolean isRegularCourseUnit() {
	return regularCourseUnit;
    }

    /**
     * @param regularCourseUnit
     *            the regularCourseUnit to set
     */
    public void setRegularCourseUnit(boolean regularCourseUnit) {
	this.regularCourseUnit = regularCourseUnit;
    }

    /**
     * @return the participants
     */
    public DataModel<User> getParticipants() {
	return participants;
    }

    /**
     * @param participants
     *            the participants to set
     */
    public void setParticipants(DataModel<User> participants) {
	this.participants = participants;
    }

    /**
     * Returns whether the page is to render in edit mode.
     * 
     * @return <code>true</code>, if to render in edit mode<br>
     *         <code>false</code>, otherwise
     */
    public boolean isEditMode() {
	return editMode;
    }

    /**
     * Sets whether the page is to be rendered in edit mode.
     * 
     * @param editMode
     *            render in edit mode
     */
    public void setEditMode(boolean editMode) {
	this.editMode = editMode;
    }

    /**
     * @return
     */
    public Date getStart() {
	return start;
    }

    /**
     * @param start
     */
    public void setStart(Date start) {
	this.start = start;
    }

    /**
     * @return
     */
    public Date getEnd() {
	return end;
    }

    /**
     * @param end
     */
    public void setEnd(Date end) {
	this.end = end;
    }

    /**
     * Returns the value of the attribute <code>courseUnit</code>.
     * 
     * @return the course unit
     */
    public CourseUnit getCourseUnit() {
	return courseUnit;
    }

    /**
     * Sets the value of the attribute <code>courseUnit</code>.
     * 
     * @param courseUnit
     *            the new value of the attribute courseUnit
     */
    public void setCourseUnit(CourseUnit courseUnit) {
	this.courseUnit = courseUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
	this.pagination.actualizeCurrentPageNumber(FacesContext
		.getCurrentInstance().getExternalContext()
		.getRequestParameterMap().get("site"));
	transaction.start();
	try {
	    this.participants.setWrappedData(CourseUnitDAO
		    .getParticipiantsOfCourseUnit(transaction, pagination,
			    courseUnitID, false));
	    this.transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fething data for pagination.");
	    this.transaction.rollback();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	// Not needed in this bean

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
     * Returns the id of the initializes course unit.
     * 
     * @return the id of the initialized course unit
     */
    public int getCourseUnitId() {
	return courseUnitID;
    }

    /**
     * Sets the id of the course unit to initialize.
     * 
     * @param courseUnitId
     *            the courseUnitId of the course unit to initialize
     */
    public void setCourseUnitId(int courseUnitId) {
	this.courseUnitID = courseUnitId;
    }

    /**
     * Returns the date that is <code>turnus</code> days more in the future than
     * the date <code>actual</code>.
     * 
     * @param actual
     *            the actual date
     * @param turnus
     *            number of day to be added to the actual date
     * @return the calculated date in the future
     */
    private Date calculateDate(Date actual, int turnus) {
	Date calculated;
	long miliseconds = actual.getTime();

	miliseconds = miliseconds + 86400000L * turnus;
	calculated = new Date(miliseconds);

	return calculated;
    }

    /**
     * @return
     */
    public boolean isDeleteAll() {
	return deleteAll;
    }

    /**
     * @param deleteAll
     */
    public void setDeleteAll(boolean deleteAll) {
	this.deleteAll = deleteAll;
    }

    /**
     * Calculates the <code>starttime</code> and the <code>endtime</code> of a
     * course unit fetched from the inserted values to be displayed properly.
     * 
     * @param courseUnit
     *            the course unit for which the starttime and endtime is
     *            calculated
     * 
     */
    @SuppressWarnings("deprecation")
    private void calculateStartAndEndTime(CourseUnit courseUnit) {
	date.setHours(start.getHours());
	date.setMinutes(start.getMinutes());
	courseUnit.setStartime(new Date(date.getTime()));

	date.setHours(end.getHours());
	date.setMinutes(end.getMinutes());
	courseUnit.setEndtime(new Date(date.getTime()));
    }

    public boolean isEditAll() {
	return editAll;
    }

    public void setEditAll(boolean editAll) {
	this.editAll = editAll;
    }

    public int getSelectedToInform() {
	return selectedToInform;
    }

    public void setSelectedToInform(int selectedToInform) {
	this.selectedToInform = selectedToInform;
    }
}