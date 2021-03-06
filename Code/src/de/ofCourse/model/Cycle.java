/**
 * This package represents all the models which are used
 */
package de.ofCourse.model;

/**
 * This class contains the information of courseunits which will take place
 * regulary
 * 
 * @author Sebastian
 *
 */
public class Cycle {

    /**
     * stores the id of the cycle
     */
    private int cycleID;

    /**
     * stores the Date and time when the first courseUnit will take place
     */
    private int courseID;
    /**
     * stores the days till the next unit should take place
     */
    private Period turnus;
    /**
     * stores the amount of units which should take place
     */
    private int numberOfUnits;

    /**
     * Returns the value of the attribute <code>courseID</code>.
     * 
     * @return the id of the associated course
     */
    public int getCourseID() {
        return this.courseID;
    }

    /**
     * Returns the value of the attribute <code>turnus</code>.
     * 
     * @return the turnus till the next unit should take place
     */
    public Period getTurnus() {
        return this.turnus;
    }

    /**
     * Returns the value of the attribute <code>numberOfUnits</code>.
     * 
     * @return the amount of units which should take place
     */
    public int getNumberOfUnits() {
        return this.numberOfUnits;
    }

    /**
     * Sets the value of the attribute <code>courseId</code>.
     * 
     * @param courseID
     *            the associated courseID
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    /**
     * Sets the value of the attribute <code>turnus</code>.
     * 
     * @param turnus
     *            new turnus to set
     */
    public void setTurnus(Period turnus) {
        this.turnus = turnus;
    }

    /**
     * Sets the value of the attribute <code>numberOfUnits</code>.
     * 
     * @param numberOfUnits
     *            new amount of Units to set
     */
    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    /**
     * Returns the id of the cycle.
     * 
     * @return the id of the cycle
     */
    public int getCycleID() {
        return cycleID;
    }

    /**
     * Sets the id of the cycle to the given value.
     * 
     * @param cycleID
     *            the given cycle id
     */
    public void setCycleID(int cycleID) {
        this.cycleID = cycleID;
    }
}
