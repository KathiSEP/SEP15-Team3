package de.ofCourse.model;

/**
 * Wraps a course unit id and a user email together.
 * 
 * @author Tobias Fuchs
 *
 */
public class MailWrapper {

    /**
     * Stores the id of a course unit
     */
    private Integer unitId;

    /**
     * Stores the mail address of a user
     */
    private String mail;

    /**
     * Constructor of the MailWrapper class
     * 
     * @param id
     *            course unit id
     * @param email
     *            mail of the user
     */
    public MailWrapper(Integer id, String email) {
	unitId = id;
	mail = email;
    }

    /**
     * Returns the unit id of the unit.
     * 
     * @return id of the course unit
     */
    public Integer getUnitId() {
	return unitId;
    }

    /**
     * Sets the id of the course unit.
     * 
     * @param unitId
     *            the unitId to set
     */
    public void setUnitId(Integer unitId) {
	this.unitId = unitId;
    }

    /**
     * Returns the mail address of the wrapper object.
     * 
     * @return the mail address
     */
    public String getMail() {
	return mail;
    }

    /**
     * Sets the mail address of the wrapper.
     * 
     * @param mail
     *            the mail to set
     */
    public void setMail(String mail) {
	this.mail = mail;
    }

}
