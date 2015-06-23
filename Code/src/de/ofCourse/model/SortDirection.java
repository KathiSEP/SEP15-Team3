package de.ofCourse.model;

public enum SortDirection {
    
    ASC("ASC"), 
    DESC("DESC");
    
    private String sortDirection;
    
    private SortDirection(String directionString) {
        this.sortDirection = directionString;
    }
    
    public String getSortDirection() {
        return this.sortDirection;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return sortDirection;
    }
     
    public static SortDirection fromBoolean(boolean sortAsc) {
        if(sortAsc) {
            return SortDirection.ASC;
        } else {
            return SortDirection.DESC;
        }
    }
    
    public static SortDirection fromString(String directionString) {
        if (directionString != null) {
          for (SortDirection direction : SortDirection.values()) {
              if (directionString.equalsIgnoreCase(direction.sortDirection)) {
                  return direction;
              }
          }
          return null;
        }
        else {
            return null;
        }
    }
}
