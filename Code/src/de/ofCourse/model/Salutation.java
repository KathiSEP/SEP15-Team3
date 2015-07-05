package de.ofCourse.model;


/**
 * represents the different salutations a User can have
 * 
 * @author Katharina Hölzl
 *
 */
public enum Salutation {
    /**
     * The user can have the salutation Mister.
     */
    MR("mr"),
    
    /**
     * Or the user can have the salutation Misses.
     */
    MS("ms");
    
    private String salutation;
    
    /**
     * 
     * @param salutationString 
     *                  the salutation string
     */
    private Salutation(String salutationString) {
        this.salutation = salutationString;
    }
    
    /**
     * 
     * @return the salutation
     */
    public String getSalutation() {
        return this.salutation;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.salutation;
    }
    
    /**
     * Assign a string which describes the salutation enum and creates the 
     * salutation
     * 
     * @param salutationString 
     *                          the salutation string
     * @return null
     */
    public static Salutation fromString(String salutationString) {
        if (salutationString != null) {
          for (Salutation salutation : Salutation.values()) {
              if (salutationString.equalsIgnoreCase(salutation.salutation)) {
                  return salutation;
              }
          }
          return null;
        }
        else {
            return null;
        }
    }
}
