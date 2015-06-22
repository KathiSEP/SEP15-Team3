/**
 * This package represents all the models which are used
 */
package de.ofCourse.model;

/**
 * represents the different statuses which the user can have
 * 
 * @author Sebastian
 *
 */
public enum UserStatus {
    /**
     * The visitor of the page is not logged in
     */
    ANONYMOUS("ANONYMOUS"),
    /**
     * The user is registrated but has not been activated yet
     */
    NOT_ACTIVATED("NOT_ACTIVATED"),
    /**
     * The user is registrated and activated
     */
    REGISTERED("REGISTERED"),
    /**
     * The user is inactiv(because he has deleted his profil or was deleted by
     * an adminstrator)
     */
    INACTIVE("INACTIVE");
    
    private String userStatus;
    
    private UserStatus(String statusString) {
        this.userStatus = statusString;
    }
    
    public String getSalutation() {
        return this.userStatus;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.name();
    }
    
    public static UserStatus fromString(String statusString) {
        if (statusString != null) {
          for (UserStatus userStatus : UserStatus.values()) {
              if (statusString.equalsIgnoreCase(userStatus.name())) {
                  return userStatus;
              }
          }
          return null;
        }
        else {
            return null;
        }
    }
}
