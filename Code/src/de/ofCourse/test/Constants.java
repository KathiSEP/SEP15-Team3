package de.ofCourse.test;

public class Constants {
    //Base URL
    public static final String localhostURL = "http://localhost:8080/OfCourse/facelets/";
    
    // Einzelne Seiten: URL und Titel
    public static final String pageIndexURL = "open/index.xhtml";
    public static final String pageIndexTitle = "OfCourse";
    public static final String pageAuthenticateURL = "open/authenticate.xhtml";
    public static final String pageAuthenticateTitle = "Anmeldung";
    public static final String pageMyCoursesURL = "user/registeredUser/myCourses.xhtml";
    public static final String pageMyCoursesTitle = "Meine Kurse";
    public static final String pageAdminManagementURL = "user/systemAdministrator/adminManagement.xhtml";
    public static final String pageAdminManagementTitle = "Seitenverwaltung";
    public static final String pageCreateCourseURL ="user/systemAdministrator/adminManagement.xhtml";
    public static final String pageCreateCourseTitle = "Neuen Kurs anlegen";
    public static final String pageActivateUsersURL ="user/courseLeader/activateUsers.xhtml";
    public static final String pageActivateUsersTitle ="Benutzeraktivierung";
    public static final String pagelistParticipantsURL ="open/courses/courseDetail.xhtml";
    public static final String pageListParticipantsTitle ="Liste aller Kursteilnehmer";
    
    // Variablenbenennung:
    // Facelet-Name in Gro�buchstaben + Typ des HTML-Elements + Beschreibung des HTML-Elements + Typ des Attributs
    
    // Alle Parameter f�r NAVIGATION
    public static final String NAVIGATIONlinkAuthenticateID = "authenticateLink";
    public static final String NAVIGATIONformNavigationID = "generalNavigationForm";
    public static final String NAVIGATIONlinkLogoutID = "logoutLink";
    
    // Alle Parameter f�r AUTHENTICATE
    public static final String AUTHENTICATEformLoginID = "formLogin";
    public static final String AUTHENTICATEinputLoginUsernameID = "usernameLogin";
    public static final String AUTHENTICATEinputLoginPasswordID = "passwordLogin";
    public static final String AUTHENTICATEformRegisterID = "fromRegister";
    public static final String AUTHENTICATEinputRegisterFirstnameID ="firstnameRegister";
    public static final String AUTHENTICATEinputRegisterLastnameID ="lastnameRegister";
    public static final String AUTHENTICATEinputRegisterUsernameID ="usernameRegister";
    public static final String AUTHENTICATEinputRegisterPasswordID ="passwordRegister";
    public static final String AUTHENTICATEinputRegisterConfirmPasswordID ="passwordConfirmRegister";
    public static final String AUTHENTICATEinputRegisterDateOfBirthID ="birthdateRegister";
    public static final String AUTHENTICATEinputRegisterStreetID ="streetRegister";
    public static final String AUTHENTICATEinputRegisterHouseNumberID ="houseNumberRegister";
    public static final String AUTHENTICATEinputRegisterCityID ="locationRegister";
    public static final String AUTHENTICATEinputRegisterZipCodeID ="zipcodeRegister";
    public static final String AUTHENTICATEinputRegisterCountryID ="countryRegister";
    public static final String AUTHENTICATEinputRegisterMailID ="emailRegister";
}
