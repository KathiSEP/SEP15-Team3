package de.ofCourse.model;

/**
 * Represents the different periods in which a regular course unit can take place.
 *
 */
public enum Period {
    
    
    /**
     * Represents a daily period
     */
    DAYS("DAYS"),
    
    /**
     * Represents a weekly period
     */
    WEEKS("WEEKS");
    
    /**
     * the period as string
     */
    private String period;
    
    /**
     * Private  constructor
     * 
     * @param periodString
     */
    private Period(String periodString) {
        this.period = periodString;
    }

    
    /**
    *Returns the String representation of the period
    * 
    */
   @Override
   public String toString() {
       return this.name();
   }
   
   /**
    * Returns a period from a given string.
    * 
    * @param periodString 
    * 		the String to get a period
    * @return the period, which was determined by the given string
    */
   public static Period fromString(String periodString) {
       if (periodString != null) {
         for (Period period : Period.values()) {
             if (periodString.equalsIgnoreCase(period.name())) {
                 return period;
             }
         }
         return null;
       }
       else {
           return null;
       }
   }   
}
