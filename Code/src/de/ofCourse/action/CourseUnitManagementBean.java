/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.CourseUnitDAO;
import de.ofCourse.databaseDAO.CycleDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.CourseRegistrationException;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Cycle;
import de.ofCourse.model.MailWrapper;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.Period;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
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

    /**
     * Represents the inform status<br>
     * Nobody of a course unit is informed in case of changes.
     */
    private static final int informNobody = 0;

    /**
     * Represents the inform status<br>
     * All participants of a course unit are informed in case of changes.
     */
    private static final int informParticipantsOfUnit = 1;
    
    /**
     * Represents the url to the course detail page
     */
    private final static String URL_COURSE_DETAIL = "/facelets/open/courses/courseDetail.xhtml";

    /**
     * Stores the selected inform status
     */
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
     * Stores whether all course units of the corresponding cycle are affected.
     */
    private boolean completeCycle;

    /**
     * Stores the unit id passed from the course details page
     */
    private int courseUnitID = 0;

    /**
     * Stores the course id passed from the course details page
     */
    private int courseID;

    /**
     * Parameter by which is sorted
     */
    private String orderParam;

    /**
     * Represents the current displayed page number.
     */
    private int currentPage;
    
    /**
     * Represents the entered turnus.
     */
    private String enteredTurnus;

    /**
     * This attribute represents a pagination object. It stores all the
     * information that is necessary for pagination, e.g. the number of elements
     * per page.
     */
    private PaginationData pagination;

    /**
     * the managed mail property
     */
    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * Initializes the page in edit mode or not.<br>
     * That depends on the pressed button in the course details bean.
     * 
     * @author Tobias Fuchs
     */
    @PostConstruct
    public void init() {
	transaction = Connection.create();

	// Fetch the mode
	String fetchedMode = FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get("editMode");

	if (fetchedMode != null && fetchedMode.toLowerCase().equals("true")) {
	    editMode = true;
	} else {
	    editMode = false;
	}

	// Fetch the ids according to the selected mode
	courseID = Integer.parseInt(FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get("courseID"));

	if (editMode) {
	    courseUnitID = Integer.parseInt(FacesContext.getCurrentInstance()
		    .getExternalContext().getRequestParameterMap()
		    .get("courseUnitID"));
	} else {
	    courseUnitID = 0;
	}

	//Initializes the bean to edit or delete a unit
	if (editMode) {
	    pagination = new PaginationData(
		    elementsPerPage, 
		    0,
		    SortColumn.NAME, 
		    SortDirection.ASC);
	    participants = new ListDataModel<User>();
	    userToAdd = new User();
	    usersToDelete = new ArrayList<User>();

	    // Initializes the pagination and if in edit mode the displayed
	    // course unit
	    transaction.start();
	    try {
		
		courseUnit = CourseUnitDAO.getCourseUnit(transaction,
			courseUnitID);
		
		if(courseUnit.getCourseAdmin() == null){
		    courseUnit.setCourseAdmin(new User());
		}

		start = new Date(courseUnit.getStartime().getTime());
		end = new Date(courseUnit.getEndtime().getTime());
		date = courseUnit.getStartime();

		if (courseUnit.getCycle() == null) {
		    courseUnit.setCycle(new Cycle());
		}

		pagination.refreshNumberOfPages(CourseUnitDAO
			.getNumberOfParticipants(transaction, courseUnitID));

		participants.setWrappedData(CourseUnitDAO
			.getParticipiantsOfCourseUnit(
				transaction, 
				pagination,
				courseUnitID,
				false));
		transaction.commit();

	    } catch (InvalidDBTransferException e) {
		LogHandler.getInstance().error(
			"Error occured during updating the"
				+ " page with elements from database.");
		transaction.rollback();
	    }
	} else {
            // Initializes the bean for creating an new unit
	    courseUnit = new CourseUnit();
	    courseUnit.setAddress(new Address());
	    courseUnit.setCourseAdmin(new User());
	    courseUnit.setCycle(new Cycle());
	    courseUnit.setCourseID(courseID);
	}
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
     * 
     * @author Tobias Fuchs
     */
    public String createCourseUnit() {
	boolean allInRange = true;
	
	//Checks whether the unit to create is in the range of the course
	if (checkDate() 
		&& checkCourseUnitLeader(
			courseID, 
			courseUnit.getCourseAdmin().getUserID())) {
	    
	    transaction.start();
	    try {
		calculateStartAndEndTime(courseUnit);
		if (regularCourseUnit) {

		    courseUnit.getCycle().setTurnus(
			    Period.fromString(enteredTurnus));
		    courseUnit.getCycle().setCycleID(
			    CycleDAO.createCycle(
				    transaction, 
				    courseID,
				    courseUnit.getCycle()));

		    Date actualStartDate = courseUnit.getStartime();
		    Date actualEndDate = courseUnit.getEndtime();

		    //Computes the next start and enddate
		    Date nextStartDate = null;
		    Date nextEndDate = null;
		    for (int i = 0; i < courseUnit.getCycle()
			    .getNumberOfUnits(); ++i) {

			if (courseUnit.getCycle().getTurnus().equals(Period.DAYS)) {
			    nextStartDate = calculateDate(actualStartDate, i
				    * day);
			    nextEndDate = calculateDate(actualEndDate, i * day);

			} else if (courseUnit.getCycle().getTurnus().equals(Period.WEEKS)) {
			    nextStartDate = calculateDate(actualStartDate, i
				    * week);
			    nextEndDate = calculateDate(actualEndDate, i * week);
			}

			// Set the calculated times
			courseUnit.setStartime(
				new Date(nextStartDate.getTime()));
			courseUnit.setEndtime(new Date(nextEndDate.getTime()));

			//Creates the course unit if it's in range of the course
			if (isUnitInRangeOfCourse(transaction,
				courseUnit.getStartime(),
				courseUnit.getEndtime())) {

			    CourseUnitDAO.createCourseUnit(transaction,
				    courseUnit, courseID, true);
			} else {
			    LogHandler.getInstance().debug("Unit "
					    + "with starttime "
					    + courseUnit.getStartime()
						    .toString()
					    + " was not edited because it's no "
					    + "more in range of the corresponding course.");
			    if(allInRange){
				allInRange = false;
			    }
			}
		    }
		} else {
		    CourseUnitDAO.createCourseUnit(
			    transaction, 
			    courseUnit,
			    courseID, 
			    false);
		}
		transaction.commit();
		if(allInRange){
		    FacesMessageCreator.createFacesMessage(
			    null,
			    sessionUser.getLabel(
				    "courseUnitManagement.createMesssage.All"));
		} else{
		    FacesMessageCreator.createFacesMessage(
			    null,
			    sessionUser.getLabel(
				    "Eine oder mehrere Kurseinheiten konnten nicht erstellt werden, da sie nicht im Zeitraum des Kurses liegen. Die restlichen Einheiten wurden erfolgreich erstellt."));
		}
		return URL_COURSE_DETAIL;

	    } catch (InvalidDBTransferException e) {
		LogHandler.getInstance().error(
			"Error occured during creating the"
				+ " a new course unit.");
		FacesMessageCreator.createFacesMessage(
				null,
				sessionUser.getLabel("courseUnitManagementBean" 
				+ ".FacesMessage.problem"));
		transaction.rollback();
	    }
	}
	return "x";

    }

    /**
     * Realizes the editing of a course unit that takes place regularly.<br>
     * Editing the course unit means that the database entry(ies) of the course
     * unit(s) is updated.<br>
     * After the edit, the participants receive a message to inform about the
     * changes.
     * 
     * @return the link to the next page
     * 
     * @author Tobias Fuchs
     */
    public String saveCourseUnit() {
	CourseUnit tempUnit = null;
	List<String> recipients = new ArrayList<String>();

	if (checkDate()) {

	    long startdate_initial = courseUnit.getStartime().getTime();
	    long enddate_initial = courseUnit.getEndtime().getTime();

	    // Update local course unit attribute with entered data
	    this.calculateStartAndEndTime(courseUnit);
	    long startdate_new = courseUnit.getStartime().getTime();
	    long enddate_new = courseUnit.getEndtime().getTime();

	    long difference_start_time = startdate_new - startdate_initial;
	    long difference_end_time = enddate_new - enddate_initial;

	    transaction.start();
	    try {
		if (completeCycle && courseUnit.getCycle() != null) {
		    List<Integer> idsToEdit = CourseUnitDAO
			    .getIdsCourseUnitsOfCycle(
				    transaction,
				    courseUnit.getCourseUnitID());

		    for (int id : idsToEdit) {
			// Fetch course unit in cycle
			recipients = new ArrayList<String>();
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
			tempUnit.setPrice(courseUnit.getPrice());
			tempUnit.setDescription(courseUnit.getDescription());
			tempUnit.setCourseAdmin(courseUnit.getCourseAdmin());
			tempUnit.setAddress(courseUnit.getAddress());

			if (isUnitInRangeOfCourse(
					transaction,
					tempUnit.getStartime(), 
					tempUnit.getEndtime())) {

			    // update tempUnit
			    CourseUnitDAO.updateCourseUnit(transaction,
				    tempUnit);

			    // Send info mail
			    List<User> participants = CourseUnitDAO
				    .getParticipiantsOfCourseUnit(
					    transaction,
					    pagination,
					    id, 
					    true);
			    if(selectedToInform == informParticipantsOfUnit){
				for(User user : participants){
				    if(UserDAO.userWantsToBeInformed(
					    transaction,
					    user.getUserID(),
					    courseID)){
					recipients.add(user.getEmail());
				    }
	                                  
				}
				if(!recipients.isEmpty()){
				    mailBean.sendCourseEditUnitMail(
					    	recipients,
					    	transaction, 
					    	id);  
	                	}
			    }       
			} else {
			    LogHandler.getInstance().debug("Unit "
					    + tempUnit.getCourseUnitID()
					    + " was not edited because it's no "
					    + "more in range of the corresponding course.");
			}
		    }

		} else {
		    CourseUnitDAO
			    .updateCourseUnit(transaction, getCourseUnit());
		    // Send info mail
		    List<User> participants = CourseUnitDAO
			    .getParticipiantsOfCourseUnit(transaction,
				    pagination, courseUnit.getCourseUnitID(),
				    true);
		    
		    if(selectedToInform == informParticipantsOfUnit){
		        for(User user : participants){
		            if(UserDAO.userWantsToBeInformed(
		        	    transaction, 
		        	    user.getUserID(),
		        	    courseID)){
	                    recipients.add(user.getEmail());
		            }      
	                }
		        
	            if(!recipients.isEmpty()){
	                mailBean.sendCourseEditUnitMail(
	                	recipients, 
	                	transaction,
	                	courseUnit.getCourseUnitID());  
	            }
		    }		    
		}
		transaction.commit();
		return URL_COURSE_DETAIL;
		
	    } catch (InvalidDBTransferException e) {
		transaction.rollback();
		FacesMessageCreator.createFacesMessage(
				null,
				sessionUser.getLabel("courseUnitManagementBean." 
				+ "FacesMessage.problem.edit"));
		LogHandler.getInstance().error(
			"Error occured during deleting" + " a course unit.");
	    }
	}
	return "x";
    }

    /**
     * Deletes the course unit from the course and returns the link to the
     * course details page.<br>
     * That means that the method deletes the course unit from the database. The
     * money that the participants have paid for the course units is
     * automatically transfered back to their accounts.
     * 
     * @return link to the course details page
     * 
     * @author Tobias Fuchs
     */
    public String deleteCourseUnit() {
	transaction.start();
	int cycleId = 0;
	List<String> mailToSend = new ArrayList<String>();
	List<MailWrapper> mailSend = new ArrayList<MailWrapper>();
	
	
	try {
	    // If all units of a cycle are to delete
	    if (courseUnit.getCycle() != null && completeCycle) {
	 
		cycleId = CycleDAO.getCycleId(
			transaction,
			courseUnit.getCourseUnitID());
		
		// Fetch the ids of all units of the cycle
		List<Integer> idsToDelete = CourseUnitDAO
			.getIdsCourseUnitsOfCycle(
				transaction,
				courseUnit.getCourseUnitID());

		for (int id : idsToDelete) {
	
		    // Fetch the participants of the unit
		    List<User> participants = CourseUnitDAO
			    .getParticipiantsOfCourseUnit(
				    transaction,
				    pagination,
				    id,
				    true);
		    
		    // Calculate for each participant the sum of prices
		    // of course units he is signed in in the complete cycle
		    for (User user : participants) {
			float amount = 0;

			for (int unitId : idsToDelete) {
			    if (UserDAO.userIsParticipantInCourseUnit(
				    transaction, user.getUserID(), unitId)) {

				amount += CourseUnitDAO.getPriceOfUnit(
					transaction,
					unitId);
				CourseUnitDAO.removeUserFromCourseUnit(
					transaction,
					user.getUserID(),
					unitId);
				if(UserDAO.userWantsToBeInformed(
					transaction, 
					user.getUserID(),
					courseID)){
				    
				mailSend.add(new MailWrapper(
					unitId,
					user.getEmail()));
				}
			    }
			}
			// Update account balance of the user
			float newBalance = amount + user.getAccountBalance();
			UserDAO.updateAccountBalance(transaction,
				user.getUserID(), newBalance);
		    }
		    
		}
		
		if(selectedToInform == informParticipantsOfUnit 
			&& !mailSend.isEmpty()){
		    sortMailAddresses(mailSend);
		}
		
		for(int id : idsToDelete ){
		    // Delete the unit
		    CourseUnitDAO.deleteCourseUnit(transaction, id);
		}
		
		// Delete the cycle
		CycleDAO.deleteCycle(transaction, cycleId);
	    } else {

		List<User> participants = CourseUnitDAO
			.getParticipiantsOfCourseUnit(
				transaction, 
				pagination,
				courseUnit.getCourseUnitID(),
				true);
		deleteSingleUnit(transaction, courseUnit.getCourseUnitID());
		
		 if(selectedToInform == informParticipantsOfUnit){
		        for(User user : participants){
		            if(UserDAO.userWantsToBeInformed(
		        	    transaction,
		        	    user.getUserID(),
		        	    courseID)){
		    mailToSend.add(user.getEmail());
		            }
		       }
		}
		 
		//Send mails
		if(!mailToSend.isEmpty()){
		mailBean.sendCourseUnitDeleteMail(
			mailToSend, 
			courseUnit.getCourseUnitID(),
			transaction);
		}
	    }
	    transaction.commit();

	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error occured during deleting" + " a course unit.");
	}
	return URL_COURSE_DETAIL;
    }

    /**
     * Fetches for each course unit that is to delete the email addresses from 
     * the given List and sends the notification mails for each course unit 
     * at once.
     * 
     * @param mailSend 
     * 		List that contains the tuples with the id of the unit to 
     * 		delete as key and the mail address of a participant of the unit 
     * 		as value
     * 
     * @author Fuchs Tobias
     */
    private void sortMailAddresses(List<MailWrapper> mailSend) {
	List<String> recipients;
	MailWrapper test;
	List<MailWrapper> toDelete;
        
        while(!mailSend.isEmpty()){
        	toDelete = new ArrayList<MailWrapper>();
            recipients = new ArrayList<String>();
            test = mailSend.iterator().next();
            recipients.add(test.getMail());
            int courseUnitid = test.getUnitId();
            
            for(MailWrapper iterator : mailSend){
            	if(iterator.getUnitId() == courseUnitid){
            		recipients.add(iterator.getMail());        
            		toDelete.add(iterator);
            	}
            }
            mailSend.removeAll(toDelete);     
            
            //Send emails
            mailBean.sendCourseUnitDeleteMail(
        	    recipients,
        	    courseUnitid,
        	    transaction);
        } 
    }

    /**
     * Deletes a single course unit from database.<br>
     * That means that all participants off this unit are signed off, the paid
     * money is transfered back and the unit is deleted.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param unitId
     *            the id of the unit that is to be deleted
     * 
     * @author Tobias Fuchs
     */
    private void deleteSingleUnit(Transaction trans, int unitId) {

	try {
	    List<User> participants = CourseUnitDAO
		    .getParticipiantsOfCourseUnit(
			    trans,
			    getPagination(), 
			    unitId,
			    true);

	    for (User user : participants) {
		CourseUnitDAO.removeUserFromCourseUnit(
			    trans, 
			    user.getUserID(),
			    unitId);
		float newAccountBalance = calculateNewAccountBalance(
			(CourseUnitDAO.getPriceOfUnit(trans, unitId)),
			user,
			false);
		UserDAO.updateAccountBalance(
			    trans, 
			    user.getUserID(),
			    newAccountBalance);
	    }
	    CourseUnitDAO.deleteCourseUnit(trans, unitId);

	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error during deleting a single course unit.");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * Calculates and returns the account balance of a user is he signs up/signs
     * off from a course unit.
     * 
     * @param courseUnit
     *            the course unit the user wants to sign up/of from
     * @param user
     *            the user whose account balance is to be updated
     * @param signUp
     *            whether its a sign up process
     * @return the updated account balance
     * @author Tobias Fuchs
     */
    private float calculateNewAccountBalance(float price, User user,
	    boolean signUp) {
	if (signUp) {

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
     * updated.
     * 
     * @author Tobias Fuchs
     */
    public void addUserToCourseUnit() {
	transaction.start();
	try {
	    float newAccountBalance = calculateNewAccountBalance(
		    courseUnit.getPrice(), 
		    userToAdd, 
		    true);
	    CourseUnitDAO.addUserToCourseUnit(
		    transaction,
		    userToAdd.getUserID(),
		    courseUnit.getCourseUnitID());
	    UserDAO.updateAccountBalance(
		    transaction, 
		    userToAdd.getUserID(),
		    newAccountBalance);

	    // Updates the shown list with the actual data
	    List<User> temp = CourseUnitDAO.getParticipiantsOfCourseUnit(
		    			transaction, 
		    			getPagination(), 
		    			courseUnitID, 
		    			false);

	    participants.setWrappedData(temp);
	    transaction.commit();
	    userToAdd = new User();
	    
	    FacesMessageCreator.createFacesMessage(
		    "formUserToAddCourseUnit:addUserCourseUnit",
		     sessionUser.getLabel("courseUnitManagementBean.FacesMessage.addUser"));

	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error during adding user manually to course unit.");

	} catch (CourseRegistrationException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error during adding user manually to course unit. "
			    + "User has not enough money to "
			    + "participate the course unit");
	}
    }

    /**
     * Checks whether the inserted start and end dates for a course unit are
     * valid.<br>
     * It is checked whether the start date is before the end date and if the
     * inserted start date is in the range of the corresponding course.
     * 
     * @return <code>true</code>, if the inserted dates are valid<br>
     *         <code>false</code>, otherwise
     * @author Tobias Fuchs
     */
    @SuppressWarnings("deprecation")
    private boolean checkDate() {

	Date tempDateBegin = new Date(date.getTime());
	Date tempDateEnd = new Date(date.getTime());
	tempDateBegin.setHours(start.getHours());
	tempDateBegin.setMinutes(start.getMinutes());
	tempDateEnd.setHours(end.getHours());
	tempDateEnd.setMinutes(end.getMinutes());

	if (tempDateBegin.getTime() > tempDateEnd.getTime()) {

	    FacesMessageCreator.createFacesMessage(null,
		    sessionUser.getLabel("courseUnitManagementBean.FacesMessage.problem.date"));
	    return false;

	} else {

	    transaction.start();

	    try {
		boolean inRange = isUnitInRangeOfCourse(transaction,
			tempDateBegin, tempDateEnd);

		// Creating the FacesMessage
		if (!inRange) {
		    Course tempCourse = CourseDAO.getCourse(transaction,
			    courseUnit.getCourseID());

		    long beginCourse = tempCourse.getStartdate().getTime();
		    long endCourse = tempCourse.getEnddate().getTime();

		    FacesMessageCreator.createFacesMessage(null,
			    sessionUser.getLabel("courseUnitManagementBean.FacesMessage.problem.check1")
				    + dateAsString(new Date(beginCourse)) + " "
				    + sessionUser.getLabel("courseUnitManagementBean.FacesMessage.problem.check2")
				    + dateAsString(new Date(endCourse)) + " !");
		}

		transaction.commit();
		return inRange;

	    } catch (InvalidDBTransferException e) {
		LogHandler.getInstance().error(
			"Error occured during fetching a course");
		transaction.rollback();
		return false;
	    }
	}
    }

    /**
     * Checks whether begin and end are in the range of the course.
     * 
     * @param transaction
     *            the transaction for database access
     * @param begin
     *            begin date
     * @param end
     *            end date
     * @return <code>true<\code>, if begin and end are in range of the course<br>
     * 	       <code>false</code>, otherwise
     */
    private boolean isUnitInRangeOfCourse(Transaction transaction, Date begin,
	    Date end) {
	boolean inRange = false;

	try {
	    Course tempCourse = CourseDAO.getCourse(transaction,
		    courseUnit.getCourseID());

	    long beginCourse = tempCourse.getStartdate().getTime();
	    long endCourse = tempCourse.getEnddate().getTime() + 86400000L;

	    if (beginCourse > begin.getTime() || end.getTime() > endCourse) {
		inRange = false;
	    } else {
		inRange = true;
	    }

	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during checking whether"
			    + " the unit is in rage of course.");
	    throw e;
	}
	return inRange;
    }

    /**
     * Returns the given date as a string in a proper format.
     * 
     * @param date
     *            the date that is to be converted to a String
     * @return the date as String
     * @author Tobias Fuchs
     */
    @SuppressWarnings("deprecation")
    private String dateAsString(Date date) {
	String dateString;
	int day = date.getDate();
	int month = (date.getMonth() + 1);
	int year = date.getYear() + 1900;

	dateString = "" + day + "." + month + "." + year;
	return dateString;

    }

    /**
     * Deletes the selected users from the course unit and transfer the paid
     * price for the course unit automatically back to the accounts of the
     * participants.<br>
     * Deleting means the users are removed from the participants list and the
     * database entry of the course unit is updated.
     * 
     * @author Tobias Fuchs
     */
    public void deleteUsersFromCourseUnit() {
	@SuppressWarnings("unchecked")
	List<User> items = (List<User>) participants.getWrappedData();
	usersToDelete = new ArrayList<User>();

	// Fetches the selected user that are to delete
	for (User item : items) {
	    if (item.isSelected()
		    && !(usersToDelete.contains(item.getUserID()))) {

		this.usersToDelete.add(item);
	    }
	}

	// Delete the users from course unit
	transaction.start();
	try {

	    for (int i = 0; i < usersToDelete.size(); ++i) {
		CourseUnitDAO.removeUserFromCourseUnit(
			transaction,
			usersToDelete.get(i).getUserID(),
			courseUnit.getCourseUnitID());

		float newAccountBalance = calculateNewAccountBalance(
			courseUnit.getPrice(), 
			usersToDelete.get(i), 
			false);

		UserDAO.updateAccountBalance(
			transaction,
			usersToDelete.get(i)
			.getUserID(),
			newAccountBalance);
	    }

	    // Updates the shown list with the actual data
	    participants.setWrappedData(CourseUnitDAO
		    .getParticipiantsOfCourseUnit(
			    transaction,
			    getPagination(),
			    courseUnitID,
			    false));
	    
	    transaction.commit();

	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during deleting users"
			    + " form course unit or while fetching"
			    + " participants to display.");
	    transaction.rollback();
	}
    }

    private boolean checkCourseUnitLeader(int courseID, int i){
	transaction.start();
	boolean isValidLeader = false;
 
	
	try{
	List<User> leaders = CourseDAO.getLeaders(transaction, courseID);
	
	if(leaders != null && leaders.size()>0){
	for(User user : leaders){
	    
	    if(user.getUserID() == i
		    && !isValidLeader){
		isValidLeader = true;
	    }  
	   
	}}
	transaction.commit();
	}catch(InvalidDBTransferException e){
	    transaction.rollback();
	}
	
	if(!isValidLeader){
	    FacesMessageCreator.createFacesMessage(null,
		    sessionUser.getLabel("courseUnitManagementBean.FacesMessage.problem.courseLeader")
		    + " " + courseID +".");
	}
	return isValidLeader;	
    }
    
    public String redirectToCourseDetails(){
	return URL_COURSE_DETAIL;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @author Tobias Fuchs
     */
    @Override
    public void goToSpecificPage() {
	pagination.setCurrentPageNumber(currentPage);
	transaction.start();

	//Fetch the participants for the next page
	try {
	    participants.setWrappedData(CourseUnitDAO
		    .getParticipiantsOfCourseUnit(
			    transaction,
			    getPagination(),
			    courseUnitID,
			    false));
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fething data for pagination.");
	    transaction.rollback();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	//Compute sort direction
	if(getPagination().getSortColumn().
		equals(SortColumn.fromString(orderParam))) {
		     
	  getPagination().changeSortDirection();
	    
	} else {
	  getPagination().setSortColumn(SortColumn.fromString(orderParam));
	}
	
	//Fetch the needed users
	transaction.start();	
	try {
	    participants.setWrappedData(CourseUnitDAO
		    .getParticipiantsOfCourseUnit(
			    transaction, 
			    getPagination(),
			    courseUnitID,
			    false));
	    transaction.commit();
	} catch (InvalidDBTransferException e) {
	    transaction.rollback();
	    LogHandler.getInstance().error(
		    "Error occured during sorting my courses");
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @author Tobias Fuchs
     */
    @Override
    public PaginationData getPagination() {
	return pagination;
    }

    /**
     * {@inheritDoc}
     * 
     * @author Tobias Fuchs
     */
    @Override
    public void setPagination(PaginationData pagination) {
	this.pagination = pagination;
    }

    /**
     * Returns the value of the attribute <code>usersToDelete</code> that stores
     * the users that are selected and shall be deleted from the course unit.
     * 
     * @return list of users to delete from the course unit
     * @author Tobias Fuchs
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
     * @author Tobias Fuchs
     */
    public void setUsersToDelete(List<User> usersToDelete) {
	this.usersToDelete = usersToDelete;
    }

    /**
     * Returns the displayed/ entered date of a course unit.
     * 
     * @return the date of the course unit
     * @author Tobias Fuchs
     */
    public Date getDate() {
	return date;
    }

    /**
     * Sets the displayed/ entered date of a course unit.
     * 
     * @param date
     *            the date to set
     * @author Tobias Fuchs
     */
    public void setDate(Date date) {
	this.date = date;
    }

    /**
     * Returns whether it is selected to edit/delete all units of a cycle.
     * 
     * @return whether all units are it be edited
     * @author Tobias Fuchs
     */
    public boolean isRegularCourseUnit() {
	return regularCourseUnit;
    }

    /**
     * Sets whether the entered course unit is regular.
     * 
     * @param regularCourseUnit
     *            the regularCourseUnit to set
     * @author Tobias Fuchs
     */
    public void setRegularCourseUnit(boolean regularCourseUnit) {
	this.regularCourseUnit = regularCourseUnit;
    }

    /**
     * @return the participants
     * @author Tobias Fuchs
     */
    public DataModel<User> getParticipants() {
	return participants;
    }

    /**
     * @param participants
     *            the participants to set
     * @author Tobias Fuchs
     */
    public void setParticipants(DataModel<User> participants) {
	this.participants = participants;
    }

    /**
     * Returns whether the page is to render in edit mode.
     * 
     * @return <code>true</code>, if to render in edit mode<br>
     *         <code>false</code>, otherwise
     * @author Tobias Fuchs
     */
    public boolean isEditMode() {
	return editMode;
    }

    /**
     * Sets whether the page is to be rendered in edit mode.
     * 
     * @param editMode
     *            render in edit mode
     * @author Tobias Fuchs
     */
    public void setEditMode(boolean editMode) {
	this.editMode = editMode;
    }

    /**
     * Returns the entered start date.
     * 
     * @return the entered start date
     * @author Tobias Fuchs
     */
    public Date getStart() {
	return start;
    }

    /**
     * Sets the entered start date.
     * 
     * @param start
     *            the entered start date
     * @author Tobias Fuchs
     */
    public void setStart(Date start) {
	this.start = start;
    }

    /**
     * Returns the entered end date.
     * 
     * @return the entered end date
     * @author Tobias Fuchs
     */
    public Date getEnd() {
	return end;
    }

    /**
     * Sets the entered end date.
     * 
     * @param end
     *            the entered date
     * @author Tobias Fuchs
     */
    public void setEnd(Date end) {
	this.end = end;
    }

    /**
     * Returns the value of the attribute <code>courseUnit</code>.
     * 
     * @return the course unit
     * @author Tobias Fuchs
     */
    public CourseUnit getCourseUnit() {
	return courseUnit;
    }

    /**
     * Sets the value of the attribute <code>courseUnit</code>.
     * 
     * @param courseUnit
     *            the new value of the attribute courseUnit
     * @author Tobias Fuchs
     */
    public void setCourseUnit(CourseUnit courseUnit) {
	this.courseUnit = courseUnit;
    }
    
    /**
     * Returns the parameter by which the elements are to be sorted.
     * 
     * @return the order parameter
     */
    public String getOrderParam() {
	return orderParam;
    }

    /**
     * Sets the parameter by which the elements are to be sorted.
     * 
     * @param orderParam
     *            the order parameter
     */
    public void setOrderParam(String orderParam) {
	this.orderParam = orderParam;
    }

    /**
     * Returns the ManagedProperty <code>SessionUser</code>.
     * 
     * @return the session of the user
     * @author Tobias Fuchs
     */
    public SessionUserBean getSessionUser() {
	return sessionUser;
    }

    /**
     * Sets the ManagedProperty <code>SessionUser</code>.
     * 
     * @param userSession
     *            session of the user
     * @author Tobias Fuchs
     */
    public void setSessionUser(SessionUserBean userSession) {
	this.sessionUser = userSession;
    }

    /**
     * Returns the id of the initializes course unit.
     * 
     * @return the id of the initialized course unit
     * @author Tobias Fuchs
     */
    public int getCourseUnitId() {
	return courseUnitID;
    }

    /**
     * Sets the id of the course unit to initialize.
     * 
     * @param courseUnitId
     *            the courseUnitId of the course unit to initialize
     * @author Tobias Fuchs
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
     * @author Tobias Fuchs
     */
    private Date calculateDate(Date actual, int turnus) {
	Date calculated;
	long miliseconds = actual.getTime();

	miliseconds = miliseconds + 86400000L * turnus;
	calculated = new Date(miliseconds);

	return calculated;
    }

    /**
     * Calculates the <code>starttime</code> and the <code>endtime</code> of a
     * course unit fetched from the inserted values to be displayed properly.
     * 
     * @param courseUnit
     *            the course unit for which the starttime and endtime is
     *            calculated
     * @author Tobias Fuchs
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

    /**
     * Returns the selected inform status.
     * 
     * @return the current inform status
     * @author Tobias Fuchs
     */
    public int getSelectedToInform() {
	return selectedToInform;
    }

    /**
     * Sets the inform status.
     * 
     * @param selectedToInform
     *            the selected inform status
     * @author Tobias Fuchs
     */
    public void setSelectedToInform(int selectedToInform) {
	this.selectedToInform = selectedToInform;
    }

    /**
     * Returns the value of the attribute <code>userToAdd</code>.
     * 
     * @return the user to add to the course unit
     * @author Tobias Fuchs
     */
    public User getUserToAdd() {
	return userToAdd;
    }

    /**
     * Sets the value of the attribute <code>usersToAdd</code>.
     * 
     * @param userToAdd
     *            the new user that is to be added to the course unit
     * @author Tobias Fuchs
     */
    public void setUserToAdd(User userToAdd) {
	this.userToAdd = userToAdd;
    }

    /**
     * Returns the current displayed page number.
     * 
     * @return the page number
     */
    public int getCurrentPage() {
	return currentPage;
    }

    /**
     * Sets the current displayed page number.
     * 
     * @param currentPage
     *            the page number
     */
    public void setCurrentPage(int currentPage) {
	this.currentPage = currentPage;
    }

    /**
     * Returns the managed property mailBean
     * 
     * @return the managed property mailBean
     * @author Tobias Fuchs
     */
    public MailBean getMailBean() {
	return mailBean;
    }

    /**
     * Sets the managed property mailBean
     * 
     * @param mailBean
     *            the managed property
     * @author Tobias Fuchs
     */
    public void setMailBean(MailBean mailBean) {
	this.mailBean = mailBean;
    }

    /**
     * Returns the inform status <code>informNobody</code>
     * 
     * @return the informnobody
     * @author Tobias Fuchs
     */
    public static int getInformnobody() {
	return informNobody;
    }

    /**
     * Returns the value of <code>completeCycle</code>
     * 
     * @return the completeCycle whether all course units are to be affected.
     * @author Tobias Fuchs
     */
    public boolean isCompleteCycle() {
	return completeCycle;
    }

    /**
     * Sets the value of <code>completeCycle</code>.
     * 
     * @param completeCycle
     *            the completeCycle to set
     * @author Tobias Fuchs
     */
    public void setCompleteCycle(boolean completeCycle) {
	this.completeCycle = completeCycle;
    }

    /**
     * Returns the value of <code>enteredTurnus</code>
     * 
     * @return the entered turnus
     */
    public String getEnteredTurnus() {
	return enteredTurnus;
    }

    /**
     * Sets the value of <code>eneteredTurnus</code>.
     * 
     * @param enteredTurnus 
     * 		the entered turnus
     */
    public void setEnteredTurnus(String enteredTurnus) {
	this.enteredTurnus = enteredTurnus;
    }
}