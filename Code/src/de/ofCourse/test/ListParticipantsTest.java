package de.ofCourse.test;

/**
 * Testing of list participants. This test is geared to the tests T40-180 and 
 * T50-40 from our product brief. 
 * The administrator is looking at the participants of the course 'Zweiter Test' 
 * and deletes one participant from this course. After that he goes back to the 
 * course detail page of the course 'Zweiter Test' and do the logout. On
 * top of that it is asserted that the faces message to the user action is 
 * correct and that the user will be sent up to the right page.
 * 
 * @author Katharina Hölzl
 */

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class ListParticipantsTest {
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
  public void testListParticipants() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
    
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Suche")).click();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    driver.findElement(By.linkText("Zweiter Test")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
    // look at list participants of the course 'Zweiter Test'
    driver.findElement(By.name("courseDetailsID:loadParticipantsID")).click();
    assert driver.findElement(By.id("listParticipants")).getText().equals("Liste aller Kursteilnehmer");
    
    // Delete one participant from this course
    driver.findElement(By.id("formListParticipants:participantsTable:1:checked")).click();
    //driver.findElement(By.linkText("Wollen Sie die ausgewählten Teilnehmer wirklich aus dem Kurs entfernen?")).click();
    //driver.findElement(By.xpath("//a[contains(text(),'Wollen Sie die ausgewählten Teilnehmer wirklich aus dem Kurs entfernen?')]")).click();
    driver.findElement(By.id("formListParticipants:delete")).click();
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Die ausgewählten Benutzer wurden erfolgreich aus dem Kurs entfernt! ");
    
    // go back to the page course detail of the course 'zweiter Test'
    driver.findElement(By.id("formBack:cancel")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
    //logout
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
