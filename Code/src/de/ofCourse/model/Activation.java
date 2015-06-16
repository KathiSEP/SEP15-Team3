package de.ofCourse.model;

/**
 * represents the different forms of activations which exists to activate a user
 * 
 * @author Katharina Hölzl
 *
 */
public enum Activation {
    /**
     * The account of a user can by activates only by e-mail verification.
     */
    EMAIL,
    
    /**
     * The account of a user must be activated by an administrator and by 
     * e-mail verification.
     */
    EMAIL_ADMIN,
    
    /**
     * The account of a user must be activated by a course leader and by 
     * e-mail verification.
     */
    EMAIL_COURSE_LEADER;
   
}
