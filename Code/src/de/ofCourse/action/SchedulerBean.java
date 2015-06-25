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
	
    
    private final int HOUR_SLOTS = 9;
	
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    private java.sql.Date currentMonday;
    
    private java.sql.Date currentSunday;
    
    private List<Week> weekDays;
    
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
    
    private void calcCurrentSunday() {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(currentMonday);
		cal.add(Calendar.DATE, +6);
		currentSunday = new Date(cal.getTime().getTime());
    }
    
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
    
    private boolean startsAtRequestedTime(CourseUnit unit, int hour) {
    	int hours = unit.getStartime().getHours();
    	
    	if (hours >= hour && hours < (hour + 2)) {
    		return true;
    	}
    	return false;
    }
    
    private Week createWeek(List<CourseUnit> weekRow) {
    	Week week = new Week();
    	Date date = new Date(currentMonday.getTime());
    	
    	for (WeekDay day : WeekDay.getWeekDays()) {
    		for (CourseUnit unit : weekRow) {
    			if (unit.getStartime().getDay() == date.getDay()) {
    				String content = getString(unit);
    				addContent(week, content, day);
    			}
    		}
    		
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		
    		//cal.roll(Calendar.DATE, true);
    		
    		cal.add(Calendar.DATE, +1);
    		date = new Date(cal.getTime().getTime());
    	}
    	return week;
    }
    
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
    	return startHours + ":" + startMinutes + " - " + endHours + ":" + endMinutes + ": " + "\n" + unitID + " " + title;
    }
    
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
    
    private void computeWeek(Date date, int interval) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, interval);
		currentMonday = new Date(cal.getTime().getTime());
		calcCurrentSunday();
		getSchedule();
    }

    private void getSchedule() {
    	transaction.start();
    	
	    try {	
	    	List<CourseUnit> weeklyUnits =
					CourseUnitDAO.getWeeklyCourseUnitsOf(transaction, sessionUser.getUserID(), currentMonday);
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

	public Date getCurrentMonday() {
		return currentMonday;
	}

	public void setCurrentMonday(Date currentMonday) {
		this.currentMonday = currentMonday;
	}

	public java.sql.Date getCurrentSunday() {
		return currentSunday;
	}

	public void setCurrentSunday(java.sql.Date currentSunday) {
		this.currentSunday = currentSunday;
	}

	public List<Week> getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(List<Week> weekDays) {
		this.weekDays = weekDays;
	}

}
