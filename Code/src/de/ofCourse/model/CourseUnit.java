/**
 *This package represents all the models which are used 
 */
package de.ofCourse.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * contains all information about a Courseunit
 * 
 * @author Sebastian
 *
 */
public class CourseUnit implements Serializable {
    
    /**
     * stores the ID of the courseUnit
     */
    private int courseUnitID;

    /**
     * stores the title of a courseunit
     */
    private String title;
    /**
     * stores the description of the courseunit (could include
     * information, needed equipment etc)
     */
    private String description; 
    
    /**
     * stores the Date and time when the CourseUnit starts.
     */
    private Date startime;
    /**
     * stores the Date and time when the CourseUnit ends.
     */
    private Date endtime;
    /**
     * stores the address where the courseunit will be hold
     */
    private Address address;
    /**
     * stores the amount the user has to pay for the courseunit to take part in
     * it
     */
    private float price;
    /**
     * stores the maximal number of Users the courseunit can handle.
     */
    private int maxUsers;
    /**
     * stores the minimal number of Users the courseunit needs to take place
     */
    private int minUsers;
    /**
     * stores the courseAdmin who will hold the CourseUnit
     */
    private User courseAdmin;
    /**
     * stores the turnus and the amount of units which still have to take
     * place(only if the CourseUnit take place with regularity)
     */
    private Cycle cycle;
    
    
    /**
     * 
     * Stores the CourseID, this CourseUnit belongs to
     */
    private int courseID;
   
    
    /**
     * @return the startime
     */
    public Date getStartime() {
        return startime;
    }

    /**
     * @param startime the startime to set
     */
    public void setStartime(Date startime) {
        this.startime = startime;
    }

    /**
     * @return the courseID
     */
    public int getCourseID() {
        return courseID;
    }

    /**
     * @param courseID the courseID to set
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    /**
     * stores the room description where the CourseUnit will be hold (Is
     * needed besides the Address for example if the CourseUnit is hold in
     * specific rooms like the gym)
     */
    private String location;

    /**
     * Returns the value of the attribute <code>title</code>.
     * 
     * @return the title of the courseUnit
     */
    public String getTitle() {
	return this.title;
    }

    /**
     * Returns the value of the attribute <code>description</code>.
     * 
     * @return the description of the courseUnit
     */
    public String getDescription() {
	return this.description;
    }


    /**
     * Returns the value of the attribute <code>endTime</code>.
     * 
     * @return the Date and the time when the courseUnit ends
     */
    public Date getEndtime() {
	return this.endtime;
    }

    /**
     * Returns the value of the attribute <code>address</code>.
     * 
     * @return the address where the courseUnit will be hold
     */
    public Address getAddress() {
	return this.address;
    }

    /**
     * Returns the value of the attribute <code>price</code>.
     * 
     * @return the price the courseUnit costs
     */
    public float getPrice() {
	return this.price;
    }

    /**
     * Returns the value of the attribute <code>maxUsers</code>.
     * 
     * @return the maximal amount of users for the course
     */
    public int getMaxUsers() {
	return this.maxUsers;
    }

    /**
     * Returns the value of the attribute <code>minUsers</code>.
     * 
     * @return the minimal amount of users needed for to the course to take place
     */
    public int getMinUsers() {
	return this.minUsers;
    }

    /**
     * Returns the value of the attribute <code>courseAdmin</code>.
     * 
     * @return the admin who leads the courseUnits
     */
    public User getCourseAdmin() {
	return this.courseAdmin;
    }

    /**
     * Returns the value of the attribute <code>cycle</code>.
     * 
     * @return the cycle which determs when the courseUnit repeats
     */
    public Cycle getCycle() {
	return this.cycle;
    }

    /**
     * Returns the value of the attribute <code>location</code>.
     * 
     * @return the room in a building where the courseUnit take place(gym. for example)
     */
    public String getLocation() {
	return this.location;
    }

    /**
     * Sets the value of the attribute <code>title</code>.
     * 
     * @param title
     *          the new title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * Sets the value of the attribute <code>description</code>.
     * 
     * @param discription
     *          the new description to set
     */
    public void setDescription(String discription) {
	this.description = discription;
    }


    /**
     * Sets the value of the attribute <code>endTime</code>.
     * 
     * @param endTime
     *          the new endTime to set
     */
    public void setEndtime(Date endTime) {
	this.endtime = endTime;
    }

    /**
     * Sets the value of the attribute <code>address</code>.
     * 
     * @param address
     *          the new address to set
     */
    public void setAddress(Address address) {
	this.address = address;
    }

    /**
     * Sets the value of the attribute <code>price</code>.
     * 
     * @param price
     *          the new price to set
     */
    public void setPrice(float price) {
	this.price = price;
    }

    /**
     * Sets the value of the attribute <code>cycle</code>.
     * 
     * @param cycle
     *          the new cycle to set
     */
    public void setCycle(Cycle cycle) {
	this.cycle = cycle;
    }

    /**
     * Sets the value of the attribute <code>maxUsers</code>.
     * 
     * @param maxUsers
     *          the new maxUsers to set
     */
    public void setMaxUsers(int maxUsers) {
	this.maxUsers = maxUsers;
    }

    /**
     * Sets the value of the attribute <code>minUsers</code>.
     * 
     * @param minUsers
     *          the new minUsers to set
     */
    public void setMinUsers(int minUsers) {
	this.minUsers = minUsers;
    }

    /**
     * Sets the value of the attribute <code>courseAdmin</code>.
     * 
     * @param courseAdmin
     *          the new courseAdmin to set
     */
    public void setCourseAdmin(User courseAdmin) {
	this.courseAdmin = courseAdmin;
    }

    /**
     * Sets the value of the attribute <code>location</code>.
     * 
     * @param location
     *          new location to set
     */
    public void setLocation(String location) {
	this.location = location;
    }

    /**
     * Returns the value of the attribute <code>leaderToAdd</code>.
     * 
     * @return the courseUnitID
     */
    public int getCourseUnitID() {
	return courseUnitID;
    }

    /**
     * Sets the value of the attribute <code>courseUnitID</code>.
     * 
     * @param courseUnitId 
     *         new courseUnitId to set
     */
    public void setCourseUnitID(int courseUnitId) {
	this.courseUnitID = courseUnitId;
    }
}
