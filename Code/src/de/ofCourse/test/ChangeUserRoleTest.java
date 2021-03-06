package de.ofCourse.test;

/**
 * Testing of change user role. This test is geared to the test T40-110 from 
 * our product brief.  
 * 
 * @author Katharina H�lzl
 */

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class ChangeUserRoleTest {
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
  public void testChangeUserRole() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Administration")).click();
    driver.findElement(By.linkText("Seitenverwaltung")).click();
    driver.findElement(By.id("userManagement:manageUsers")).click();
    
    new Select(driver.findElement(By.id("formFilterCourses:filterSearchParam"))).selectByVisibleText("Nutzername");
    driver.findElement(By.id("formFilterCourses:filterInput")).clear();
    driver.findElement(By.id("formFilterCourses:filterInput")).sendKeys("Kathi5");
    driver.findElement(By.id("formFilterCourses:searchUser")).click();
    driver.findElement(By.id("formResultTable:users:0:details")).click();
    driver.findElement(By.id("formSettings:changeSettings")).click();
    new Select(driver.findElement(By.id("formChangeSettings:userRoleSelection"))).selectByVisibleText("Kursleiter");
    driver.findElement(By.id("formChangeSettings:saveSettings")).click();
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Ihre Benutzerdaten wurden erfolgreich ge�ndert! ");
    
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
