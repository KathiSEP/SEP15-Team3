/**
 * * This package represents all the models which are used
 */
package de.ofCourse.model;

/**
 * represents the different days within a calendar week
 * 
 * @author Patrick Cretu
 *
 */
public enum WeekDay {

	/**
	 * The week's Monday
	 */
	MONDAY,
	
	/**
	 * The week's Monday
	 */
	TUESDAY,
	
	/**
	 * The week's Monday
	 */
	WEDNESDAY,
	
	/**
	 * The week's Monday
	 */
	THURSDAY,
	
	/**
	 * The week's Monday
	 */
	FRIDAY,
	
	/**
	 * The week's Monday
	 */
	SATURDAY,
	
	/**
	 * The week's Monday
	 */
	SUNDAY;
	
	/**
	 * Returns an array containing all week days.
	 * 
	 * @return an array containing all week days
	 */
	public static WeekDay[] getWeekDays() {
		return WeekDay.values();
	}
	
}
