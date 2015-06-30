package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class AuthenticationTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  public static final String facesMessages= "facesMessages";
  public static final String messageUsernameLogin= "messageUsernameLogin";
  public static final String messagePasswordLogin= "messagePasswordLogin";

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testAuthentication() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    // Testing required messages
    driver.findElement(By.id("formLogin:login")).click();
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageUsernameLogin)).getText().contains("Bitte geben Sie Ihren Benutzernamen ein. ");
    assert driver.findElement(By.id(messagePasswordLogin)).getText().contains("Bitte geben Sie Ihr Passwort ein. ");
    
    // Testing user name and password length
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("adm");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("adm");
    driver.findElement(By.id("formLogin:login")).click();
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(messageUsernameLogin)).getText().contains("Ihr Benutzername muss zwischen 5 und 100 Zeichen lang sein. ");
    assert driver.findElement(By.id(messagePasswordLogin)).getText().contains("Ihr Passwort muss zwischen 8 und 100 Zeichen lang sein. ");
    
    // Testing wrong password
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("abgerdsfres");
    driver.findElement(By.id("formLogin:login")).click();
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Benutzername oder Passwort falsch! ");
    
    // Testing wrong user name
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin12");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Benutzername oder Passwort falsch! ");
    
    // Testing correct user name and password
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    assert driver.findElement(By.id("myCoursesPage")).getText().equals("Meine Kurse");
    
    driver.findElement(By.id("generalNavigationForm:logoutLink")).click();
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
