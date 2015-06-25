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
    
    private Salutation(String salutationString) {
        this.salutation = salutationString;
    }
    
    public String getSalutation() {
        return this.salutation;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.name();
    }
    
    public static Salutation fromString(String salutationString) {
        if (salutationString != null) {
          for (Salutation salutation : Salutation.values()) {
              if (salutationString.equalsIgnoreCase(salutation.name())) {
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
