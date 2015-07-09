/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.ofCourse.databaseDAO.CourseUnitDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Week;
import de.ofCourse.model.WeekDay;
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
@ViewScoped
public class SchedulerBean {
	
    
	/**
	 * The number of time slots in the scheduler
	 */
    private final int HOUR_SLOTS = 9;
	
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    /**
     * The Monday's date of the current week
     */
    private java.sql.Date currentMonday;
    
    /**
     * The Sunday's date of the current week
     */
    private java.sql.Date currentSunday;
    
    /**
     * Stores the scheduler's tuples of the current week which is displayed
     */
    private List<Week> weekDays;
    
    /**
     * Stores each week day's calendar value
     */
    private List<String> days;
    
    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

	@PostConstruct
    public void init() {
    	transaction = Connection.create();
    	transaction.start();
    	
    	try {
    		String currentDate = CourseUnitDAO.getCurrentWeekDay(transaction);
    		currentMonday = getCurrentMonday(transaction, currentDate);
    		transaction.commit();
    		
    		calcCurrentSunday();
    		getSchedule();
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "init()");
    		transaction.rollback();
    	}
    }
    
	/**
	 * Calculates the gap between the passed date and that week's Monday and
	 * returns the date of the required Monday
	 * 
	 * @param trans
	 * 			  the Transaction object which contains the connection to the
     *            database
	 * @param currentDate
	 *            the passed date as a string value
	 * @return the date of the requested week's Monday
	 */
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
            .error("SQL Exception occoured during executing " +
                    "getCurrentMonday(Transaction trans, " +
                    "String currentDate)");
    		transaction.rollback();
    	}
		return currentMonday;
	}
    
    /**
     * Calculates the date of the current week's Sunday based on the current
     * week's Monday
     */
    private void calcCurrentSunday() {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(currentMonday);
		cal.add(Calendar.DATE, +6);
		currentSunday = new Date(cal.getTime().getTime());
    }
    
    /**
     * Returns a week tuple of course units beginning at the passed time
     * 
     * @param weeklyUnits
     *                  the list containing all the user's course unit of the
     *                  current requested week
     * @param hour
     *           the time slot within the scheduler
     * @return a week tuple of course units beginning at the passed time
     */
    private Week getWeekTuple(List<CourseUnit> weeklyUnits, int hour) {
    	List<CourseUnit> weekRow = new ArrayList<CourseUnit>();
    	String hourString  = hour + ":00 - \n" + (hour + 2) + ":00";
    	
    	for (CourseUnit unit : weeklyUnits) {
    		if (startsAtRequestedTime(unit, hour)) {
    			weekRow.add(unit);
    		}
    	}
    	
    	Week week = createWeek(weekRow);
    	week.setHour(hourString);
    	return week;
    }
    
    /**
     * Checks if the passed course unit starts at the passed time
     * 
     * @param unit
     *           the course unit which start time is to be checked
     * @param hour
     *           the start time of the required time slot
     * @return true, if the course unit starts at the required time, false
     * 		   otherwise
     */
    @SuppressWarnings("deprecation")
	private boolean startsAtRequestedTime(CourseUnit unit, int hour) {
		int hours = unit.getStartime().getHours();
    	
    	if (hours >= hour && hours < (hour + 2)) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Creates a week tuple containing the course units which start
     * at the required time
     * 
     * @param weekRow
     *              the list of course units which start within the time slot
     * @return a week tuple containing the course units within the time slot
     */
    @SuppressWarnings("deprecation")
	private Week createWeek(List<CourseUnit> weekRow) {
    	Week week = new Week();
    	Date date = new Date(currentMonday.getTime());
    	
    	days = new ArrayList<String>();
    	String calDay = String.valueOf(currentMonday).substring(8);
		days.add(calDay);
    	
    	for (WeekDay day : WeekDay.getWeekDays()) {
    		for (CourseUnit unit : weekRow) {
    			if (unit.getStartime().getDay() == date.getDay()) {
    				String content = getString(unit);
    				addContent(week, content, day);
    			}
    		}
    		
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		cal.add(Calendar.DATE, +1);
    		date = new Date(cal.getTime().getTime());
    		
    		if (days.size() < 7) {
    			calDay = String.valueOf(date).substring(8);
    			days.add(calDay);
    		}
    	}
    	
    	return week;
    }
    
    /**
     * Formats the labels of a specific slot in the scheduler
     * 
     * @param unit
     *           the course unit containing the values of which the label is
     *           created
     * @return the label displayed in the scheduler
     */
    @SuppressWarnings("deprecation")
	private String getString(CourseUnit unit) {
    	
    	String unitID = String.valueOf(unit.getCourseUnitID());
    	String title = unit.getTitle();
    	String startHours = String.valueOf(unit.getStartime().getHours());
    	int startMinutesInt = unit.getStartime().getMinutes();
    	String startMinutes = String.valueOf(startMinutesInt);
    	String endHours = String.valueOf(unit.getEndtime().getHours());
    	int endMinutesInt = unit.getEndtime().getMinutes();
    	String endMinutes = String.valueOf(endMinutesInt);
    	
    	if (startMinutesInt < 10) {
    		startMinutes += "0";
    	}
    	if (endMinutesInt < 10) {
    		endMinutes += "0";
    	}
    	return startHours + ":" + startMinutes + " - " + endHours + ":" +
    		endMinutes + ": " + "\n" + unitID + " " + title;
    }
    
    /**
     * Adds the passed content to the week day's label
     * 
     * @param week
     *           the passed week tuple of the required time slot
     * @param content
     *              the text content added to the label
     * @param numDay
     *             the week day to which the content is added
     */
    private void addContent(Week week, String content, WeekDay numDay) {
    	switch (numDay) {
    	case MONDAY:
    		String mon = week.getMonday();
    		if (mon != null) {
    			content = mon + "\n" + content;
    		}
    		week.setMonday(content);
    		break;
    	case TUESDAY:
    		String tue = week.getTuesday();
    		if (tue != null) {
    			content = tue + "\n" + content;
    		}
    		week.setTuesday(content);
    		break;
    	case WEDNESDAY:
    		String wed = week.getWednesday();
    		if (wed != null) {
    			content = wed + "\n " + content;
    		}
    		week.setWednesday(content);
    		break;
    	case THURSDAY:
    		String thu = week.getThursday();
    		if (thu != null) {
    			content = thu + "\n" + content;
    		}
    		week.setThursday(content);
    		break;
    	case FRIDAY:
    		String fri = week.getFriday();
    		if (fri != null) {
    			content = fri + "\n" + content;
    		}
    		week.setFriday(content);
    		break;
    	case SATURDAY:
    		String sat = week.getSaturday();
    		if (sat != null) {
    			content = sat + "\n" + content;
    		}
    		week.setSaturday(content);
    		break;
    	case SUNDAY:
    		String sun = week.getSunday();
    		if (sun != null) {
    			content = sun + "\n" + content;
    		}
    		week.setSunday(content);
    		break;
    	}
    }
	
    /**
     * Swaps the actual displayed week in the scheduler to the following week.
     * The method checks whether there is a next week to display. If this is
     * true, it displays the scheduler of the following week.
     */
    public void displayNextWeek() {
    	computeWeek(currentSunday, +1);
    }
    
    /**
     * Swaps the actual displayed week in the scheduler to the previous week.
     * The method checks whether there is a previous week to display. If this is
     * true, it displays the scheduler of the previous week.
     */
    public void displayPreviousWeek() {
    	computeWeek(currentMonday, -7);
    }
    
    /**
     * Computes the Monday's date of the requested week and executes method
     * calls to compute the weekly schedule
     * 
     * @param date
     *           the passed date as a lead to compute the Monday
     * @param interval
     *               the interval of days added to the passed date
     */
    private void computeWeek(Date date, int interval) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, interval);
		currentMonday = new Date(cal.getTime().getTime());
		calcCurrentSunday();
		getSchedule();
    }

    /**
     * Retrieves the user's course units of the requested week and computes
     * the displayed schedule table
     */
    private void getSchedule() {
    	transaction = Connection.create();
    	transaction.start();
    	
	    try {	
	    	List<CourseUnit> weeklyUnits =
					CourseUnitDAO.getWeeklyCourseUnitsOf(transaction,
							sessionUser.getUserID(), currentMonday);
			List<Week> week = new ArrayList<Week>();
			int startHour = 6;
			
			for (int i = 0; i < HOUR_SLOTS; i++) {
				week.add(getWeekTuple(weeklyUnits, startHour));
				startHour += 2;
			}
			
			weekDays = week;
			transaction.commit();
		} catch (InvalidDBTransferException e) {
			LogHandler
	        .getInstance()
	        .error("SQL Exception occoured during executing "
	                + "init()");
			transaction.rollback();
		}
    	
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
     * 
     * @return the current week's Monday
     */
	public Date getCurrentMonday() {
		return currentMonday;
	}

	/**
	 * Sets the current week's Monday
	 * 
	 * @param currentMonday
	 *                    the Monday's date
	 */
	public void setCurrentMonday(Date currentMonday) {
		this.currentMonday = currentMonday;
	}

	/**
	 * 
	 * @return the current week's Sunday
	 */
	public java.sql.Date getCurrentSunday() {
		return currentSunday;
	}

	/**
	 * Sets the current week's Sunday
	 * 
	 * @param currentSunday
	 *                    the Sunday's date
	 */
	public void setCurrentSunday(java.sql.Date currentSunday) {
		this.currentSunday = currentSunday;
	}

	/**
	 * 
	 * @return the list of week tuples
	 */
	public List<Week> getWeekDays() {
		return weekDays;
	}

	/**
	 * Sets the currents week's list of tuples
	 * 
	 * @param weekDays
	 *               the list of week tuples
	 */
	public void setWeekDays(List<Week> weekDays) {
		this.weekDays = weekDays;
	}

	/**
	 * 
	 * @return the list of the week's calendar days
	 */
	public List<String> getDays() {
		return days;
	}

	/**
	 * Sets the list of the week's calendar days
	 * 
	 * @param days
	 *           the week's calendar days
	 */
	public void setDays(List<String> days) {
		this.days = days;
	}

}
