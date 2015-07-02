package de.ofCourse.test;

/**
 * Testing of registration. This test is geared to the test T30-30 from our 
 * product brief. Furthermore there are a view more tests for the faults that 
 * can appear because of invalid user inserts. 
 * The user 'Kathi6' will be created in this test. On top of that it is 
 * asserted that the faces messages to the user insert are correct and that the 
 * user will be sent up to the right page.
 * 
 * @author Katharina Hölzl
 */

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class RegistrationTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  public static final String messageUsernameRegister= "messageUsernameRegister";
  public static final String messagePasswordRegister= "messagePasswordRegister";
  public static final String messagePasswordConfirmRegister= "messagePasswordConfirmRegister";
  public static final String messageDateOfBirthRegister= "messageDateOfBirthRegister";
  public static final String messageHouseNumberRegister= "messageHouseNumberRegister";
  public static final String messageCityRegister= "messageZipCodeRegister";
  public static final String messageCountryRegister= "messageZipCodeRegister";
  public static final String messageZipCodeRegister= "messageZipCodeRegister";
  public static final String messageEmailRegister= "messageEmailRegister";
  public static final String facesMessages= "facesMessages";

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testRegistration() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    
    // Testing required messages
    new Select(driver.findElement(By.id("formRegister:titleRegister"))).selectByVisibleText("Frau");
    driver.findElement(By.id("formRegister:firstnameRegister")).clear();
    driver.findElement(By.id("formRegister:firstnameRegister")).sendKeys("Katharina");
    driver.findElement(By.id("formRegister:register")).click();
    
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageUsernameRegister)).getText().contains("Bitte geben Sie einen Benutzernamen ein. ");
    assert driver.findElement(By.id(messagePasswordRegister)).getText().contains("Bitte geben Sie ein Passwort ein. ");
    assert driver.findElement(By.id(messagePasswordConfirmRegister)).getText().contains("Bitte geben Sie ein Passwort zur Bestätigung ein. ");
    assert driver.findElement(By.id(messageCityRegister)).getText().contains("Bitte geben Sie eine Stadt ein. ");
    assert driver.findElement(By.id(messageZipCodeRegister)).getText().contains("Bitte geben Sie eine Postleitzahl ein. ");
    assert driver.findElement(By.id(messageCountryRegister)).getText().contains("Bitte geben Sie ein Land ein. ");
    assert driver.findElement(By.id(messageEmailRegister)).getText().contains("Bitte geben Sie ihre E-Mail Adresse ein. ");
    
    // Testing user name is already existing
    driver.findElement(By.id("formRegister:lastnameRegister")).clear();
    driver.findElement(By.id("formRegister:lastnameRegister")).sendKeys("Hoelzl");
    driver.findElement(By.id("formRegister:usernameRegister")).clear();
    driver.findElement(By.id("formRegister:usernameRegister")).sendKeys("admin1");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("blablabal");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("blablabal");
    driver.findElement(By.id("formRegister:locationRegister")).clear();
    driver.findElement(By.id("formRegister:locationRegister")).sendKeys("Fuerstenzell");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("94081");
    driver.findElement(By.id("formRegister:countryRegister")).clear();
    driver.findElement(By.id("formRegister:countryRegister")).sendKeys("Deutschland");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina_hoelzl@web.de");
    driver.findElement(By.id("formRegister:selectAGB")).click();
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageUsernameRegister)).getText().contains("Der Benutzername existiert bereits. ");
    
    // Testing AGB not accepted
    driver.findElement(By.id("formRegister:usernameRegister")).clear();
    driver.findElement(By.id("formRegister:usernameRegister")).sendKeys("Kathi6");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Bitte bestätigen Sie die AGB's! ");
   
    // Testing mail is already existing
    driver.findElement(By.id("formRegister:selectAGB")).click();
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina_hoelzl@web.de");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Die angegebene E-Mail existiert bereits! ");
    
    // Testing wrong mail format
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina_hoelzl");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageEmailRegister)).getText().contains("Kein gültiges E-Mail Format. ");
    
    //Testing mail is already existing (upper cases instead of lower cases)
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("Katharina_hoelzl@web.de");
    driver.findElement(By.id("formRegister:selectAGB")).click();
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Die angegebene E-Mail existiert bereits! ");
    
    // Testing wrong insert format (no number)
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:birthdateRegister")).clear();
    driver.findElement(By.id("formRegister:birthdateRegister")).sendKeys("test");
    driver.findElement(By.id("formRegister:houseNumberRegister")).clear();
    driver.findElement(By.id("formRegister:houseNumberRegister")).sendKeys("test");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("test");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina.hoelzl934@gmx.de");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageDateOfBirthRegister)).getText().contains("Datum existiert nicht oder Format (dd.MM.yyyy) ist falsch. ");
    assert driver.findElement(By.id(messageHouseNumberRegister)).getText().contains("Bitte geben Sie eine positive Zahl als Hausnummer ein. ");
    assert driver.findElement(By.id(messageZipCodeRegister)).getText().contains("Bitte geben Sie eine positive Zahl als Postleitzahl ein. ");
    
    // Testing date of birth is in the future and zip code is too lang
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:birthdateRegister")).clear();
    driver.findElement(By.id("formRegister:birthdateRegister")).sendKeys("29.05.2016");
    driver.findElement(By.id("formRegister:houseNumberRegister")).clear();
    driver.findElement(By.id("formRegister:houseNumberRegister")).sendKeys("");
    driver.findElement(By.id("formRegister:locationRegister")).clear();
    driver.findElement(By.id("formRegister:locationRegister")).sendKeys("Engertsham");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("9408112312345");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina.hoelzl934@gmx.de");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageDateOfBirthRegister)).getText().contains("Datum liegt in der Zukunft oder mehr als 150 Jahre zurück. ");
    assert driver.findElement(By.id(messageZipCodeRegister)).getText().contains("Ihre Eingabe muss zwischen 1 und 10 Zeichen lang sein. ");
    
    // Testing passwords not equal
    driver.findElement(By.id("formRegister:birthdateRegister")).clear();
    driver.findElement(By.id("formRegister:birthdateRegister")).sendKeys("29.05.1993");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("94081");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messagePasswordRegister)).getText().contains("Die Passwörter müssen übereinstimmen. ");
    
    // Testing correct insert of all data
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:streetRegister")).clear();
    driver.findElement(By.id("formRegister:streetRegister")).sendKeys("Am Kastenfeld");
    driver.findElement(By.id("formRegister:houseNumberRegister")).clear();
    driver.findElement(By.id("formRegister:houseNumberRegister")).sendKeys("39");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:register")).click();
    assertTrue(closeAlertAndGetItsText().contains("Nach erfolgreicher Registrierung wird eine Bestätigungsmail an ihre Mail-Adresse geschickt. Bestätigen Sie bitte den darin enthaltenen Verifizierungslink."));
    assert driver.findElement(By.id("indexPage")).getText().equals("OfCourse");
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
