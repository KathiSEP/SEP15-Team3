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
    EMAIL("EMAIL"),
    
    /**
     * The account of a user must be activated by an administrator and by 
     * e-mail verification.
     */
    EMAIL_ADMIN("EMAIL_ADMIN"),
    
    /**
     * The account of a user must be activated by a course leader and by 
     * e-mail verification.
     */
    EMAIL_COURSE_LEADER("EMAIL_COURSE_LEADER");
    
private String activationType;
    
    private Activation(String activationString) {
        this.activationType = activationString;
    }
    
    public String getActivation() {
        return this.activationType;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.name();
    }
    
    public static Activation fromString(String activationString) {
        if (activationString != null) {
          for (Activation activation : Activation.values()) {
              if (activationString.equalsIgnoreCase(activation.name())) {
                  return activation;
              }
          }
          return null;
        }
        else {
            return null;
        }
    }
   
}
