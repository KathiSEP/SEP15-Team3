package de.ofCourse.test;

import org.junit.BeforeClass;
import org.junit.Test;

import de.ofCourse.model.Address;
import de.ofCourse.model.User;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

public class RegistrationTest {
    
    private static User registerUser = new User();
    private static Address registerAddress = new Address();
    
    private static String registerUserPassword;
    private static String registerUserConfirmPassword;
    private static String registerUserDateOfBirth;
    private static String registerUserHouseNumberWrong;
    private static String registerAddressZipCodeWrong;
    
    @BeforeClass
    public static void prepare() {
        setBaseUrl(Constants.localhostURL);
        
        registerUser.setFirstname("Katharina");
        registerUser.setLastname("Hölzl");
        registerUser.setUsername("Kathi123");
        
        registerUserDateOfBirth = "29.05.1993";      
        registerUserPassword = "Kathi123#";
        registerUserConfirmPassword = "Kathi123#";
        
        registerUserHouseNumberWrong = "test";
        registerAddressZipCodeWrong = "test";
        
        registerAddress.setStreet("Am Kastenfeld 39");
        registerAddress.setHouseNumber(39);
        registerAddress.setZipCode(94081);
        registerAddress.setCity("Füstenzell");
        registerAddress.setCountry("Deutschland");
        
        registerUser.setEmail("katharina_hoelzl@web.de");
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
       
        
        //Test auf "Benutzername existiert bereits"
        
        // Input-Feld mit Daten belegen
        selectOptionByValue(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEmenuRegisterTitle, "ms");
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterFirstnameID, registerUser.getFirstname());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterLastnameID, registerUser.getLastname());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterUsernameID, registerUser.getUsername());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterDateOfBirthID, registerUserDateOfBirth);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterStreetID, registerAddress.getStreet());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterHouseNumberID, registerAddress.getHouseNumber().toString());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterCityID, registerAddress.getCity());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterZipCodeID, registerAddress.getZipCode().toString());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterCountryID, registerAddress.getCountry());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, registerUser.getEmail());
        checkCheckbox(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGBID);
        assertCheckboxSelected (Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGBID);
        
        // Formulardaten abschicken
        submit();
        
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertEquals(getElementById(Constants.AUTHENTICATEmessagesRegisterUsernameID).getTextContent(), "Der Benutzername existiert bereits. ");
          
        
        
        //Test auf "E-Mail existiert bereits"
        
        registerUser.setUsername("Kathi1234");
        registerUserPassword = "Kathi1234#";
        registerUserConfirmPassword = "Kathi1234#";
        
        // Input-Feld mit Daten belegen
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterUsernameID, registerUser.getUsername());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, registerUser.getEmail());
        
        // Formulardaten abschicken
        submit();
        
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEGlobalFacesMessages).getTextContent().contains("Die angegebene E-Mail existiert bereits!"));
        
        
        // Test auf falsches Eingabeformat bei Hausnummer, Zipcode und Geburtsdatum
        
        registerUserDateOfBirth = "test";
        
        // Input-Feld mit Daten belegen
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterUsernameID, registerUser.getUsername());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterHouseNumberID, registerUserHouseNumberWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterZipCodeID, registerAddressZipCodeWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterDateOfBirthID, registerUserDateOfBirth);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, registerUser.getEmail());
        
        // Formulardaten abschicken
        submit();
        
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterDateOfBirthID).getTextContent().contains("Datum existiert nicht oder Format (dd.MM.yyyy) ist falsch."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterHouseNumberID).getTextContent().contains("Bitte geben Sie eine positive Zahl als Hausnummer ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterZipCodeID).getTextContent().contains("Bitte geben Sie eine positive Zahl als Postleitzahl ein."));
        
     
        
        
    }
}
