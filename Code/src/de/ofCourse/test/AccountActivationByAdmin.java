package de.ofCourse.test;

/**
 * Testing of account activation by the administrator. This test is geared to 
 * the test T40-20 from our product brief. 
 * The user 'Kathi6' will be activated by the administrator in this test. On 
 * top of that it is asserted that the faces messages are correct and that the 
 * user will be sent up to the right page.
 * 
 * @author Katharina Hölzl
 */

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AccountActivationByAdmin {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  public static final String facesMessages= "facesMessages";

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testAccountActivationByAdmin() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    
    // Login as administrator
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Administration")).click();
    driver.findElement(By.linkText("Benutzer aktivieren")).click();
    assert driver.findElement(By.id("userActivation")).getText().equals("Benutzeraktivierung");
    
    // Testing activate account
    driver.findElement(By.id("formActivateUsers:participantsTable:0:checked")).click();
    driver.findElement(By.id("formActivateUsers:activate")).click();
    //Check alert and close
    assertTrue(closeAlertAndGetItsText().contains("Wollen Sie die ausgewählten Teilnehmer wirklich aktivieren?"));
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Der Benutzer wurde erfolgreich aktiviert! ");
    
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
