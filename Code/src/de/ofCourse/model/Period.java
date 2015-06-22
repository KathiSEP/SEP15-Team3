package de.ofCourse.model;

public enum Period {
    
    
    DAYS("DAYS"),
    WEEKS("WEEKS");
    
 private String period;
    
    private Period(String periodString) {
        this.period = periodString;
    }
    
    public String getSalutation() {
        return this.period;
    }
    
    
    /*
    * (non-Javadoc)
    * @see java.lang.Enum#toString()
    */
   @Override
   public String toString() {
       return this.name();
   }
   
   public static Period fromString(String statusString) {
       if (statusString != null) {
         for (Period period : Period.values()) {
             if (statusString.equalsIgnoreCase(period.name())) {
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
