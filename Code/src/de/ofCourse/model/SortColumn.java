package de.ofCourse.model;

/**
 * represents the different forms of columms a table has
 * 
 * @author Katharina Hölzl
 *
 */
public enum SortColumn {
    
    NICKNAME("nickname"), 
    EMAIL("email"), 
    COURSENEWS("courseNews"), 
    ID("id"),
    FIRST_NAME("first_name"), 
    NAME("name"), 
    DATE_OF_BIRTH("date_of_birth"),
    TITLE("title"),
    LOCATION("location"),
    START_TIME("start_time"),
    MAX_USERS("max_participants"),
    START_DATE("start_date"),
    END_DATE("end_date");
    
    private String sortColumn;
    
    /**
     * 
     * @param columnString
     *                  the string column
     */
    private SortColumn(String columnString) {
        this.sortColumn = columnString;
    }
    
    /**
     * 
     * @return the sortColumn
     */
    public String getSortColumn() {
        return this.sortColumn;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return sortColumn;
    }
    
    /**
     * Assign a string which describes the sort column enum and creates the 
     * sort column
     * 
     * @param columnString 
     *                  the column string
     * @return ID
     */
    public static SortColumn fromString(String columnString) {
        if (columnString != null) {
          for (SortColumn column : SortColumn.values()) {
              if (columnString.equalsIgnoreCase(column.sortColumn)) {
                  return column;
              }
          }
          return ID;
        }
        else {
            return null;
        }
    }
}
