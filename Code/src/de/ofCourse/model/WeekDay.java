package de.ofCourse.model;

public enum WeekDay {

	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY,
	SUNDAY;
	
	public static WeekDay[] getWeekDays() {
		return WeekDay.values();
	}
	
}
