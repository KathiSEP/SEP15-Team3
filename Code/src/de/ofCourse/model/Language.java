package de.ofCourse.model;

/**
 * represents the different forms of languages a user can select
 * 
 * @author Katharina Hölzl
 *
 */
public enum Language {
    DE("DE"),
    BAY("BAY"),
    EN("EN");
    
    private String langString;
    
    /**
     * 
     * @param langString language string
     */
    private Language(String langString) {
        this.langString = langString;
    }
    
    /**
     * 
     * @return the language string
     */
    public String getLanguage() {
        return this.langString;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return langString;
    }
    
    /**
     * Assign a string which describes the language enum and creates the 
     * language
     * 
     * @param langString
     *                  language string
     * @return null
     */
    public static Language fromString(String langString) {
        if (langString != null) {
          for (Language language : Language.values()) {
              if (langString.equalsIgnoreCase(language.langString)) {
                  return language;
              }
          }
          return null;
        }
        else {
            return null;
        }
    }
}
