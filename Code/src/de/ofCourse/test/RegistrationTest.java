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
    private static String registerUserDateOfBirthWrong;
    private static String registerUserConfirmPasswordWrong;
    private static String registerUserPasswordFormatWrong;
    private static String registerUserPasswordLengthWrong;
    
    @BeforeClass
    public static void prepare() {
        setBaseUrl(Constants.localhostURL);
        
        registerUser.setFirstname("Katharina");
        registerUser.setLastname("Hölzl");
        registerUser.setUsername("Kathi123");
        
        registerUserDateOfBirth = "29.05.1993";      
        registerUserPassword = "Kathi1234#";
        registerUserConfirmPassword = "Kathi1234#";
        
        registerUserHouseNumberWrong = "test";
        registerAddressZipCodeWrong = "test";
        registerUserConfirmPasswordWrong = "Kathi12345#";
        registerUserPasswordFormatWrong = "Kathilein";
        registerUserPasswordLengthWrong = "Kathi";
        
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
       
        
   //Test auf required-Messages
        selectOptionByValue(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEmenuRegisterTitle, "ms");
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterFirstnameID, registerUser.getFirstname());
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterUsernameID).getTextContent().contains("Bitte geben Sie einen Benutzernamen ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterPasswordID).getTextContent().contains("Bitte geben Sie ein Passwort ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterConfirmPasswordID).getTextContent().contains("Bitte geben Sie ein Passwort zur Bestätigung ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterCityID).getTextContent().contains("Bitte geben Sie eine Stadt ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterZipCodeID).getTextContent().contains("Bitte geben Sie eine Postleitzahl ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterCountryID).getTextContent().contains("Bitte geben Sie ein Land ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterMailID).getTextContent().contains("Bitte geben Sie ihre E-Mail Adresse ein."));
        
        
   //Test auf "Benutzername existiert bereits"
        
        // Input-Feld mit Daten belegen
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
        //checkCheckbox(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGBID);
        //assertCheckboxSelected (Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGBID);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertEquals(getElementById(Constants.AUTHENTICATEmessagesRegisterUsernameID).getTextContent(), "Der Benutzername existiert bereits. ");
         
        
   //Test auf AGB nicht bestätigt
        
        registerUser.setUsername("Kathi1234");
        
        // Input-Feld mit Daten belegen
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterUsernameID, registerUser.getUsername());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEGlobalFacesMessages).getTextContent().contains("Bitte bestätigen Sie die AGB's!"));
         
        
   //Test auf "E-Mail existiert bereits"
        
        // Input-Feld mit Daten belegen
        
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, registerUser.getEmail());
        checkCheckbox(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEcheckboxRegisterAGBID);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEGlobalFacesMessages).getTextContent().contains("Die angegebene E-Mail existiert bereits!"));
       
        
        
        
   // Test auf falsches Mail-Format
        
        registerUser.setEmail("katharina_hoelzl");
        
        // Input-Feld mit Daten belegen
       
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, registerUser.getEmail());
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterMailID).getTextContent().contains("Kein gültiges E-Mail Format."));
      
        
   // Test auf falsches Eingabeformat bei Hausnummer, Zipcode und Geburtsdatum
        
        registerUserDateOfBirthWrong = "test";
        registerUser.setEmail("katharina.hoelzl93@gmx.de");
        
        // Input-Feld mit Daten belegen
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterUsernameID, registerUser.getUsername());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterHouseNumberID, registerUserHouseNumberWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterZipCodeID, registerAddressZipCodeWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterDateOfBirthID, registerUserDateOfBirthWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterMailID, registerUser.getEmail());
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterDateOfBirthID).getTextContent().contains("Datum existiert nicht oder Format (dd.MM.yyyy) ist falsch."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterHouseNumberID).getTextContent().contains("Bitte geben Sie eine positive Zahl als Hausnummer ein."));
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterZipCodeID).getTextContent().contains("Bitte geben Sie eine positive Zahl als Postleitzahl ein."));
        
     
   // Test auf Datum in der Zukunft
        
        registerUserDateOfBirthWrong = "29.05.2016";
        
        // Input-Feld mit Daten belegen
       
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterHouseNumberID, registerAddress.getHouseNumber().toString());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterZipCodeID, registerAddress.getZipCode().toString());
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterDateOfBirthID, registerUserDateOfBirthWrong);
        
        // Formulardaten abschicken
        submit();
        
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterDateOfBirthID).getTextContent().contains("Datum liegt in der Zukunft oder mehr als 150 Jahre zurück."));
       
        
   //Test auf unterschiedliche Passwörter
        
        
        // Input-Feld mit Daten belegen
        
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPasswordWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterDateOfBirthID, registerUserDateOfBirth);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterPasswordID).getTextContent().contains("Die Passwörter müssen übereinstimmen."));
       
        
   //Test auf falsches PasswortFormat
        
        // Input-Feld mit Daten belegen
        
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPasswordFormatWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserPasswordFormatWrong);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterPasswordID).getTextContent().contains("Das Passwort muss mindestens ein Sonderzeichen (@,#,$,%,!,_,?,&), Ziffern und Groß- und Kleinbuchstaben enthalten. ß, ä, ö, ü sind nicht erlaubt."));
        
        
//Test auf falsche PasswortLänge
        
        // Input-Feld mit Daten belegen
        
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPasswordLengthWrong);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserPasswordLengthWrong);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageAuthenticateTitle);
        
        // Facesmessage überprüfen
        assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterPasswordID).getTextContent().contains("Das Passwort muss mindestens 8 Zeichen lang sein, darf aber höchstens 100 Zeichen lang sein. "));
        
        
   //Test auf korrekte Eingabe aller Daten und Anlegen des Benutzers
        
        // Input-Feld mit Daten belegen
        
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterPasswordID, registerUserPassword);
        setTextField(Constants.AUTHENTICATEformRegisterID + ":" + Constants.AUTHENTICATEinputRegisterConfirmPasswordID, registerUserConfirmPassword);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der Index vergleichen
        assertTitleEquals(Constants.pageIndexTitle);
        
        // Facesmessage überprüfen
        //assertTrue(getElementById(Constants.AUTHENTICATEmessagesRegisterPasswordID).getTextContent().contains("Das Passwort muss mindestens 8 Zeichen lang sein, darf aber höchstens 100 Zeichen lang sein. "));
           

  
        
    }
}
