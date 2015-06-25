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
    
    private Language(String langString) {
        this.langString = langString;
    }
    
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
