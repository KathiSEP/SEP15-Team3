package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * 
 * @author Patrick Cretu
 *
 */
public class UploadProfileImageTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private final String userImage = "userImage"; 
  private final String editProfilePicture = "editProfilePicture";

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testUploadProfileImage() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("Slash");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!1");
    driver.findElement(By.id("formLogin:login")).click();
    
    driver.findElement(By.linkText("Profil")).click();
    driver.findElement(By.id("formUpload:changeProfilePicture")).click();
    driver.findElement(By.id("formUpload:userImage")).clear();
    driver.findElement(By.id("formUpload:userImage")).sendKeys("C:\\Users\\Patrick\\Music\\0004-05-wrong tunes.m4r");
    driver.findElement(By.id("formUpload:editProfilePicture")).click();
    assert driver.findElement(By.id(userImage)).getText().equals("Kein gültiges Bildformat.  ");
    
    driver.findElement(By.id("formUpload:userImage")).clear();
    driver.findElement(By.id("formUpload:userImage")).sendKeys("C:\\Users\\Patrick\\Pictures\\stick.jpg");
    driver.findElement(By.id("formUpload:editProfilePicture")).click();
    assert driver.findElement(By.id(editProfilePicture)).getText().equals("Ihr Profilbild wurde erfolgreich geändert ");
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
