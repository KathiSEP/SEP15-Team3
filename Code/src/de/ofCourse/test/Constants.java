package de.ofCourse.test;

public class Constants {
    public static final String localhostURL = "http://localhost:8080/OfCourse/facelets/";
    
    // Einzelne Seiten: URL und Titel
    public static final String pageAuthenticateURL = "open/authenticate.xhtml";
    public static final String pageAuthenticateTitle = "Anmeldung";
    public static final String pageMyCoursesURL = "user/registeredUser/myCourses.xhtml";
    public static final String pageMyCoursesTitle = "Meine Kurse";
    public static final String pageIndexURL = "open/index.xhtml";
    public static final String pageIndexTitle = "OfCourse";
    
    // Variablenbenennung:
    // Facelet-Name in Großbuchstaben + Typ des HTML-Elements + Beschreibung des HTML-Elements + Typ des Attributs
    
    // Alle Parameter für NAVIGATION
    public static final String NAVIGATIONlinkAuthenticateID = "authenticateLink";
    public static final String NAVIGATIONformNavigationID = "generalNavigationForm";
    public static final String NAVIGATIONlinkLogoutID = "logoutLink";
    
    // Alle Parameter für AUTHENTICATE
    public static final String AUTHENTICATEformLoginID = "formLogin";
    public static final String AUTHENTICATEinputLoginUsernameID = "usernameLogin";
    public static final String AUTHENTICATEinputLoginPasswordID = "passwordLogin";
}
