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
    // Facelet-Name in Großbuchstaben + Typ des HTML-Elements + Beschreibung des HTML-Elements + Typ des Attributs 
    // = " ID des Felders im Facelet"
    
    // Alle Parameter für NAVIGATION
    public static final String NAVIGATIONlinkAuthenticateID = "authenticateLink";
    public static final String NAVIGATIONformNavigationID = "generalNavigationForm";
    public static final String NAVIGATIONlinkLogoutID = "logoutLink";
    
    // Alle Parameter für AUTHENTICATE
    public static final String AUTHENTICATEformLoginID = "formLogin";
    public static final String AUTHENTICATEinputLoginUsernameID = "usernameLogin";
    public static final String AUTHENTICATEinputLoginPasswordID = "passwordLogin";
    public static final String AUTHENTICATEmessagesLoginUsernameID ="messageUsernameLogin";
    public static final String AUTHENTICATEmessagesLoginPasswordID = "messagePasswordLogin";
    
    public static final String AUTHENTICATEformRegisterID = "formRegister";
    public static final String AUTHENTICATEmenuRegisterTitle = "titleRegister";
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
    public static final String AUTHENTICATEcheckboxRegisterAGBID = "selectAGB";
    public static final String AUTHENTICATEmessagesRegisterUsernameID = "messageUsernameRegister";
    public static final String AUTHENTICATEmessagesRegisterFirstnameID ="messageFirstNameRegister";
    public static final String AUTHENTICATEmessagesRegisterLastnameID = "messageLastNameRegister";
    public static final String AUTHENTICATEmessagesRegisterDateOfBirthID = "messageDateOfBirthRegister";
    public static final String AUTHENTICATEmessagesRegisterPasswordID = "messagePasswordRegister";
    public static final String AUTHENTICATEmessagesRegisterConfirmPasswordID = "messagePasswordConfirmRegister";
    public static final String AUTHENTICATEmessagesRegisterStreetID = "messageStreetRegister";
    public static final String AUTHENTICATEmessagesRegisterHouseNumberID = "messageHouseNumberRegister";
    public static final String AUTHENTICATEmessagesRegisterZipCodeID = "messageZipCodeRegister";
    public static final String AUTHENTICATEmessagesRegisterCityID = "messageCityRegister";
    public static final String AUTHENTICATEmessagesRegisterCountryID = "messageCountryRegister";
    public static final String AUTHENTICATEmessagesRegisterMailID = "messageEmailRegister";
    
    public static final String AUTHENTICATEformForgotPasswordID = "formForgotPassword";
    public static final String AUTHENTICATEinputForgotPasswordMailID = "passwordEMail";
    public static final String AUTHENTICATEmessagesForgotPasswordMailID = "messageMailForgotPassword";
    
    public static final String AUTHENTICATEGlobalFacesMessages = "facesMessages";
    
    // Alle Parameter für CreateCourse
    public static final String CREATECOURSEformCreateCourseID ="formCreateCourse";
    public static final String CREATECOURSEinputTitleID = "courseTitle";
    public static final String CREATECOURSEinputDescriptionID = "courseDescription";
    public static final String CREATECOURSEinputCourseLeaderID = "IdOfCourseLeader";
    public static final String CREATECOURSEinputMaxParticipantsID = "courseParticipants";
    public static final String CREATECOURSEinputStartdateID = "courseStartDate";
    public static final String CREATECOURSEinputEnddateID = "courseEndDate"; 
    public static final String CREATECOURSEinputPictureID = "courseImage";
    
    // Alle Parameter für AchtivateUsers
    public static final String ACTIVATEUSERSformActivateUsersID = "formActivateUsers";
    public static final String ACTIVATEUSERScheckboxActivationID = "checked";
    
    //Alle Paramter für listParticipants
    public static final String LISTPARTICIPANTSformBackToCourseDetailsID = "formBack";
    public static final String LISTPARTICIPANTScommandButtonBackToCourseDetailsID = "cancel";
    public static final String LISTPARTICIPANTSformListParticipantsID = "formListParticipants";
    public static final String LISTPARTICIPANTScheckboxDeleteID = "checked";
    
}
