package de.ofCourse.test;

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Checks whether a single course unit for course Yoga is created.
 * 
 * @author Tobias Fuchs
 *
 */
public class CreateYogaUnitTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testCreateYogaUnit() throws Exception {
    driver.get(baseUrl + "OfCourse/");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("Basti3");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Muhmuh1!");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Suche")).click();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    driver.findElement(By.linkText("Yoga")).click();
    driver.findElement(By.name("courseDetailsID:j_idt87")).click();
    
    assert driver.findElement(By.id("headingCreatedUnit")).getText().equals("Kurseinheit anlegen");
    driver.findElement(By.id("formCourseUnit:dateCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:dateCourseUnit")).sendKeys("05.08.2015");
    driver.findElement(By.id("formCourseUnit:startTimeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:startTimeCourseUnit")).sendKeys("17:00");
    driver.findElement(By.id("formCourseUnit:endTimeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:endTimeCourseUnit")).sendKeys("19:00");
    driver.findElement(By.id("formCourseUnit:locationCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:locationCourseUnit")).sendKeys("Turnhalle");
    driver.findElement(By.id("formCourseUnit:descriptionCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:descriptionCourseUnit")).sendKeys("Bringe K�rper, Geist und Seele in Einklang durch Yoga�bungen. Jeder Teilnehmer sollte seine eigene Turnmatte mitnehmen.");
    driver.findElement(By.id("formCourseUnit:leaderCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:leaderCourseUnit")).sendKeys("10001");
    driver.findElement(By.id("formCourseUnit:minUsersCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:minUsersCourseUnit")).sendKeys("5");
    driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).sendKeys("30");
    driver.findElement(By.id("formCourseUnit:titleCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:titleCourseUnit")).sendKeys("Yogakurs");
    driver.findElement(By.id("formCourseUnit:houseNumberCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:houseNumberCourseUnit")).sendKeys("5");
    driver.findElement(By.id("formCourseUnit:cityCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:cityCourseUnit")).sendKeys("Musterstadt");
    driver.findElement(By.id("formCourseUnit:streetCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:streetCourseUnit")).sendKeys("Musterstrasse");
    driver.findElement(By.id("formCourseUnit:zipcodeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:zipcodeCourseUnit")).sendKeys("99999");
    driver.findElement(By.id("formCourseUnit:countryCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:countryCourseUnit")).sendKeys("Deutschland");
    driver.findElement(By.id("formCourseUnit:createCourseUnit")).click();
    
    assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Kursdetails");
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
