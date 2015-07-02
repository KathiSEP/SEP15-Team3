package de.ofCourse.test;

/**
 * Testing of add leader to course. This test is geared to the test T50-50 from 
 * our product brief. Furthermore there are a view more tests for the faults that 
 * can appear because of invalid user inserts. 
 * The user with the id '10009' will be added to the course as leader in this 
 * test. On top of that it is asserted that the faces messages to the user 
 * insert are correct and that the user will be sent up to the right page.
 * 
 * @author Katharina Hölzl
 */

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AddLeaderToCourseTest {
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
  public void testAddLeaderToCourse() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    
    //Login as administrator
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Suche")).click();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    driver.findElement(By.linkText("Zweiter Test")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
    // Testing insert is a negative number
    driver.findElement(By.name("courseDetailsID:edit")).click();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("-1234");
    driver.findElement(By.name("courseDetailsID:addCourseLeader")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Eingegebene Kursleiter-ID ist keine positive Zahl! ");
    
    // Testing insert is no number
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("wertdsg");
    driver.findElement(By.name("courseDetailsID:addCourseLeader")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Bitte geben Sie eine positive Zahl als KursleiterID ein.");
    
    
    
    // Testing inserted id does not exist
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("123456");
    driver.findElement(By.name("courseDetailsID:addCourseLeader")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
    assert driver.findElement(By.id(facesMessages)).getText().contains("Die eingegebene Kursleiter-ID existiert nicht im System.");
    
    // Testing insert correct course instructor id
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("10009");
    driver.findElement(By.name("courseDetailsID:addCourseLeader")).click();
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Zweiter Test");
    
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
