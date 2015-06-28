package de.ofCourse.test;

import org.junit.BeforeClass;
import org.junit.Test;

import de.ofCourse.model.User;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

public class RegistrationTest {
    
    private static String RegisterUserFirstName;
    private static String RegisterUserLastName;
    private static String RegisterUserNickname;
    private static String RegisterUserPassword;
    private static String RegisterUserConfirmPassword;
    private static String RegisterUserDateOfBirth;
    private static String RegisterUserStreet;
    private static String RegisterUserHouseNumber;
    private static String RegisterUserCity;
    private static String RegisterUserZipCode;
    private static String RegisterUserCountry;
    private static String RegisterUserMail;
    private static String RegisterUserAGB;
    
    
    @BeforeClass
    public static void prepare() {
        setBaseUrl(Constants.localhostURL);
        
        RegisterUserFirstName = "Katharina";
        RegisterUserLastName = "Hölzl";
        RegisterUserNickname = "Kathi123";
        RegisterUserPassword = "Kathi123#";
        RegisterUserConfirmPassword = "Kathi123#";
        RegisterUserDateOfBirth = "29.05.1993";
        RegisterUserStreet = "Am Kastenfeld";
        RegisterUserHouseNumber = "39";
        RegisterUserCity = "Fürstenzell";
        RegisterUserZipCode = "94081";
        RegisterUserCountry = "Deutschland";
        RegisterUserMail = "Katharina_hoelzl@web.de";
    }
    
    /**
     * Beginn des Testablaufs.
     */
    @Test
    public void testLogin() {
        
     // Auf Startseite beginnen
        beginAt(Constants.pageIndexURL);
        
        // Titel der Startseite vergleichen
        assertTitleEquals(Constants.pageIndexTitle);
        
        // Zu Anmelden-Seite navigieren
        clickLink(Constants.NAVIGATIONformNavigationID + ":" + Constants.NAVIGATIONlinkAuthenticateID);
        
        // Titel der Anmelden-Seite vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Bei Eingabe von Formulardaten überprüfen, ob das Form-Element auf der Website vorhanden ist
        assertFormPresent(Constants.AUTHENTICATEformRegisterID);
        
        // Form-Element auswählen
        setWorkingForm(Constants.AUTHENTICATEformRegisterID);
        
        // Input-Feld mit Daten belegen
        selectOptionByValue(Constants.AUTHENTICATEformRegisterID + ":" + "titleRegister", "ms");
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterFirstnameID, RegisterUserFirstName);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterLastnameID, RegisterUserLastName);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterUsernameID, RegisterUserNickname);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, RegisterUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, RegisterUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterDateOfBirthID, RegisterUserDateOfBirth);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterStreetID, RegisterUserStreet);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterHouseNumberID, RegisterUserHouseNumber);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterCityID, RegisterUserCity);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterZipCodeID, RegisterUserZipCode);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterCountryID, RegisterUserCountry);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, RegisterUserMail);
        checkCheckbox(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGB);
        assertCheckboxSelected (Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGB);
        
        // Formulardaten abschicken
        submit();
        
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        assertEquals(getElementById("messageUsernameRegister").getTextContent(), "Der Benutzername existiert bereits. ");
             
    }
}
