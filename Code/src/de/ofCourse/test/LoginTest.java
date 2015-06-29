package de.ofCourse.test;

import org.junit.BeforeClass;
import org.junit.Test;

import de.ofCourse.model.User;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class LoginTest { 
    
    private static User loginUser = new User();
    private static String loginUserPassword;
    
    @BeforeClass
    public static void prepare() {
        setBaseUrl(Constants.localhostURL);
        
        loginUser.setUsername("admin1");
        loginUserPassword = "Password!123";
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
        assertFormPresent(Constants.AUTHENTICATEformLoginID);
        
        // Form-Element auswählen
        setWorkingForm(Constants.AUTHENTICATEformLoginID);
        
        // Input-Feld mit Daten belegen
        setTextField(Constants.AUTHENTICATEformLoginID + ":" + Constants.AUTHENTICATEinputLoginUsernameID, loginUser.getUsername());
        setTextField(Constants.AUTHENTICATEformLoginID + ":" + Constants.AUTHENTICATEinputLoginPasswordID, loginUserPassword);
        
        // Formulardaten abschicken
        submit();
        
        // Titel der MyCourses-Seite vergleichen
        assertTitleEquals(Constants.pageMyCoursesTitle);
        
        // Auf Logout Link klicken
        clickLink(Constants.NAVIGATIONformNavigationID + ":" + Constants.NAVIGATIONlinkLogoutID);
        
        // Titel der Anmelden-Seite vergleichen
        assertTitleEquals(Constants.pageIndexTitle);
    }

}
