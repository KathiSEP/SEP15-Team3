package de.ofCourse.model;

public enum SortColumn {
    
    NICKNAME("nickname"), 
    EMAIL("email"), 
    COURSENEWS("courseNews"), 
    ID("id"),
    FIRST_NAME("first_name"), 
    NAME("name"), 
    DATE_OF_BIRTH("date_of_birth"),
    TITEL("title"),
    LOCATION("location"),
    START_TIME("start_time");
    
    private String sortColumn;
    
    private SortColumn(String columnString) {
        this.sortColumn = columnString;
    }
    
    public String getSortColumn() {
        return this.sortColumn;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.name();
    }
    
    public static SortColumn fromString(String columnString) {
        if (columnString != null) {
          for (SortColumn column : SortColumn.values()) {
              if (columnString.equalsIgnoreCase(column.name())) {
                  return column;
              }
          }
          return null;
        }
        else {
            return null;
        }
    }
}
