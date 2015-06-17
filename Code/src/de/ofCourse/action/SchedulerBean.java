/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import de.ofCourse.Database.dao.CourseUnitDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Week;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Displays the personal scheduler of an user.<br>
 * In this scheduler the course units of a user are displayed in a graphical
 * way. If the user registers for a new course unit, it is added automatically
 * to the scheduler. If the user signs off from a course unit, it is
 * automatically removed from the scheduler.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>scheduler</code>.
 * 
 * @author Patrick Cretu
 *
 */
@ManagedBean
@RequestScoped
public class SchedulerBean {

    
	
	private String thisDate;
	private Date monday;
	
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    private Map<Integer, List<List<CourseUnit>>> weekDays
	  = new HashMap<Integer, List<List<CourseUnit>>>(7);
    
    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

	@PostConstruct
    public void init() {
		
		//
		setThisDate("heute");
		System.out.println("in init in SchedulerBean");
		
    	transaction = Connection.create();
    	transaction.start();
    	
    	try {
    		String currentDate = CourseUnitDAO.getCurrentWeekDay(transaction);
    		
    		//
    		setThisDate(currentDate);
    		System.out.println("today: " + currentDate);
    		
    		Date currentMonday = getCurrentMonday(transaction, currentDate);
    		
    		//
    		setMonday(currentMonday);
    		System.out.println("monday: " + currentMonday);
    		
    		List<CourseUnit> weeklyUnits =
    				CourseUnitDAO.getWeeklyCourseUnitsOf(transaction, sessionUser.getUserID(), currentMonday);
    		List<Week> week = new ArrayList<Week>();
    		int hour = 6;
    		
    		for (int i = 0; i < 9; i++) {
    			week.add(getWeekTuple(weeklyUnits, hour));
    			hour += 2;
    		}
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "init()");
    		transaction.rollback();
    	}
    }
    
    private Date getCurrentMonday(Transaction trans, String currentDate) {
    	Date currentMonday = null;
    	int gap;
    	String[] tokens = currentDate.split("\\s+");
    	String currentDay = tokens[0];
    	
    	switch (currentDay) {
    	case "Tue":
    		gap = 1;
    		break;
    	case "Wed":
    		gap = 2;
    		break;
    	case "Thu":
    		gap = 3;
    		break;
    	case "Fri":
    		gap = 4;
    		break;
    	case "Sat":
    		gap = 5;
    		break;
    	case "Sun":
    		gap = 6;
    		break;
    	default:
    		gap = 0;
    	}
		
    	try {
    		currentMonday = CourseUnitDAO.getCurrentMonday(trans, gap);
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "getCurrentMonday(Transaction trans, String currentDate)");
    		transaction.rollback();
    	}
		return currentMonday;
	}
    
    private Week getWeekTuple(List<CourseUnit> weeklyUnits, int hour) {
    	Week week = null;
    	
    	for (int i = 0; i < weeklyUnits.size(); i++) {
    		if (startsAtRequestedTime(weeklyUnits.get(i), hour)) {
    			
    		}
    	}
    	
    	return week;
    }
    
    private boolean startsAtRequestedTime(CourseUnit unit, int hour) {
    	int hours = unit.getStartime().getHours();
    	if (hours >= 6 && hours < 8) {
    		return true;
    	}
    	return false;
    }

	public void getUnit(String day, int hour) {
    	
    }

    /**
     * Swaps the actual displayed week in the scheduler to the following week.
     * The method checks whether there is a next week to display. If this is
     * true, it displays the scheduler of the following week.
     */
    public void displayNextWeek() {
    }

    /**
     * Swaps the actual displayed week in the scheduler to the previous week.
     * The method checks whether there is a previous week to display. If this is
     * true, it displays the scheduler of the previous week.
     */
    public void displayPreviousWeek() {
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

	public Map<Integer, List<List<CourseUnit>>> getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(Map<Integer, List<List<CourseUnit>>> weekDays) {
		this.weekDays = weekDays;
	}

	public Date getMonday() {
		return monday;
	}

	public void setMonday(Date monday) {
		this.monday = monday;
	}

	public String getThisDate() {
		return thisDate;
	}

	public void setThisDate(String thisDate) {
		this.thisDate = thisDate;
	}

}
