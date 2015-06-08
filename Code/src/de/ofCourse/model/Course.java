/**
 * This package represents all the models which are used
 */
package de.ofCourse.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Contains all information about a course
 * 
 * @author Sebastian
 *
 */
public class Course implements Serializable {
    
    /**
     * Stroes the ID the specific course gets
     * 
     */

    private int courseID;
    
    /**
     * Stores the coursetitel 
     * 
     */
    private String title;
    /**
     * Stores the description of the course (could include information,
     * needed equipment etc)
     * 
     */
    private String description;
    /**
     * Stores the next CourseUnit of the Course which will be hold.
     * 
     */
    private CourseUnit nextCourseUnit;
    /**
     * Stores the Date when the Course will start.
     * 
     */
    private Date startdate;
    /**
     * Stores the Date when the Course will end.
     * 
     */
    private Date enddate;
    /**
     * Stores the maximal amount of Users the Course can handle.
     * 
     */
    private Integer maxUsers;
    /**
     * Stores the Logo of the Course
     */
    private String courseImage;
   

    /**
     * Returns the value of the attribute <code>title</code>.
     * 
     * @return the title of the Course
     */
    public String getTitle() {
	return title;
    }

    /**
     * Returns the value of the attribute <code>description</code>.
     * 
     * @return the description of the Course
     */
    public String getDescription() {
	return description;
    }

    /**
     * Returns the value of the attribute <code>nextCourseUnit</code>.
     * 
     * @return the nextCourseUnit which will be hold in the Course
     */
    public CourseUnit getNextCourseUnit() {
	return nextCourseUnit;
    }

    /**
     * Returns the value of the attribute <code>startDate</code>.
     * 
     * @return the Date when the Course will start or has started
     */
    public Date getStartdate() {
	return startdate;
    }

    /**
     * Returns the value of the attribute <code>endDate</code>.
     * 
     * @return the Date when the Course will end
     */
    public Date getEnddate() {
	return enddate;
    }

    /**
     * Returns the value of the attribute <code>maxUsers</code>.
     * 
     * @return the maximal amount of Users the Course can handle
     */
    public Integer getMaxUsers() {
	return maxUsers;
    }

    /**
     * Returns the value of the attribute <code>courseImage</code>.
     * 
     * @return the logo of the Course
     */
    public String getCourseImage() {
	return courseImage;
    }


    /**
     * Sets the value of the attribute <code>title</code>.
     *
     * @param title
     *          new course title
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * Sets the value of the attribute <code>description</code>.
     * 
     * @param description
     *          new course description to set
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * Sets the value of the attribute <code>nextCourseUnit</code>.
     * 
     * @param nextCourseUnit
     *          new nextCourseUnit to set
     */
    public void setNextCourseUnit(CourseUnit nextCourseUnit) {
	this.nextCourseUnit = nextCourseUnit;
    }

    /**
     * Sets the value of the attribute <code>startDate</code>.
     * 
     * @param startDate
     *          new startDate to set
     */
    public void setStartdate(Date startDate) {
	this.startdate = startDate;
    }

    /**
     * Sets the value of the attribute <code>endDate</code>.
     * 
     * @param endDate
     *          new endDate to set
     */
    public void setEnddate(Date endDate) {
	this.enddate = endDate;
    }

    /**
     * Sets the value of the attribute <code>maxUsers</code>.
     * 
     * @param maxUsers
     *          new maximum of users the course can handle to set
     */
    public void setMaxUsers(Integer maxUsers) {
	this.maxUsers = maxUsers;
    }

    

    /**
     * Sets the value of the attribute <code>image</code>.
     * 
     * @param image
     *          new course logo to set
     */
    public void setCourseImage(String image) {
	this.courseImage = image;
    }

    /**
     *  Returns the value of the attribute <code>courseID</code>.
     * 
     * @return the courseID
     */
    public int getCourseID() {
	return courseID;
    }

    /**
     * Sets the value of the attribute <code>courseID</code>.
     * 
     * @param courseID 
     *          new courseID to set 
     */
    public void setCourseID(int courseID) {
	this.courseID = courseID;
    }

}
