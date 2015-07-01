package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class CreateYogaUnitsTest {
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
  public void testCreateYogaUnits() throws Exception {
     driver.get(baseUrl + "OfCourse/");
     driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
     driver.findElement(By.id("formLogin:usernameLogin")).clear();
     driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("Basti3");
     driver.findElement(By.id("formLogin:passwordLogin")).clear();
     driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Basti#249");
     driver.findElement(By.id("formLogin:login")).click();
     driver.findElement(By.id("leadedCoursesList:courseTable:0:details")).click();
     driver.findElement(By.name("courseDetailsID:j_idt87")).click();
    
    assert driver.findElement(By.id("headingCreatedUnit")).getText().equals("Kurseinheit anlegen");
    driver.findElement(By.id("formCourseUnit:titleCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:titleCourseUnit")).sendKeys("");  
    driver.findElement(By.id("formCourseUnit:dateCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:dateCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:startTimeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:startTimeCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:endTimeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:endTimeCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:locationCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:locationCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:streetCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:streetCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:houseNumberCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:houseNumberCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:cityCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:cityCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:zipcodeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:zipcodeCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:countryCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:countryCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:descriptionCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:descriptionCourseUnit")).sendKeys("");
    driver.findElement(By.id("formCourseUnit:leaderCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:leaderCourseUnit")).sendKeys("10005");
    driver.findElement(By.id("formCourseUnit:minUsersCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:minUsersCourseUnit")).sendKeys("5");
    driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).sendKeys("30");
    driver.findElement(By.id("formCourseUnit:createCourseUnit")).click();
    
    assert driver.findElement(By.id("titleCourseUnitMessage")).getText().contains("Bitte geben Sie einen Titel ein.");
    assert driver.findElement(By.id("dateCourseUnitMessage")).getText().contains("Bitte geben Sie einen Termin ein.");
    assert driver.findElement(By.id("startTimeCourseUnitMessage")).getText().contains("Bitte geben Sie einen Beginn ein.");
    assert driver.findElement(By.id("endTimeCourseUnitMessage")).getText().contains("Bitte geben Sie ein Ende ein."); 
    assert driver.findElement(By.id("locationCourseUnitMessage")).getText().contains("Bitte geben Sie einen Ort ein.");
    assert driver.findElement(By.id("streetCourseUnitMessage")).getText().contains("Bitte geben Sie eine Strasse ein.");
    assert driver.findElement(By.id("houseNumberCourseUnitMessage")).getText().contains("Bitte geben Sie eine Hausnummer ein.");
    assert driver.findElement(By.id("cityCourseUniMessage")).getText().contains("Bitte geben Sie eine Stadt ein.");
    assert driver.findElement(By.id("zipcodeCourseUnitMessage")).getText().contains("Bitte geben Sie eine Postleitzahl ein.");
    assert driver.findElement(By.id("descriptionCourseUnitMessage")).getText().contains("Bitte geben Sie eine Beschreibung ein.");
    assert driver.findElement(By.id("countryCourseUnitMessage")).getText().contains("Bitte geben Sie ein Land ein.");
    
    
    assert driver.findElement(By.id("headingCreatedUnit")).getText().equals("Kurseinheit anlegen");  
    driver.findElement(By.id("formCourseUnit:titleCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:titleCourseUnit")).sendKeys("Yogakurs");  
    driver.findElement(By.id("formCourseUnit:dateCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:dateCourseUnit")).sendKeys("12.8.2015");
    driver.findElement(By.id("formCourseUnit:startTimeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:startTimeCourseUnit")).sendKeys("17:00");
    driver.findElement(By.id("formCourseUnit:endTimeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:endTimeCourseUnit")).sendKeys("19:00");
    driver.findElement(By.id("formCourseUnit:locationCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:locationCourseUnit")).sendKeys("Turnhalle");
    driver.findElement(By.id("formCourseUnit:streetCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:streetCourseUnit")).sendKeys("Musterstrasse");
    driver.findElement(By.id("formCourseUnit:houseNumberCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:houseNumberCourseUnit")).sendKeys("5");
    driver.findElement(By.id("formCourseUnit:cityCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:cityCourseUnit")).sendKeys("Musterstadt");
    driver.findElement(By.id("formCourseUnit:zipcodeCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:zipcodeCourseUnit")).sendKeys("99999");
    driver.findElement(By.id("formCourseUnit:countryCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:countryCourseUnit")).sendKeys("Deutschland");
    driver.findElement(By.id("formCourseUnit:descriptionCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:descriptionCourseUnit")).sendKeys("Bringe Koerper, Geist und Seele in Einklang durch Yogauebungen. Jeder Teilnehmer sollte seine eigene Turnmatte mitnehmen.");
    driver.findElement(By.id("formCourseUnit:leaderCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:leaderCourseUnit")).sendKeys("10005");
    driver.findElement(By.id("formCourseUnit:minUsersCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:minUsersCourseUnit")).sendKeys("5");
    driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).clear();
    driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).sendKeys("30");
    
    driver.findElement(By.id("formCourseUnit:cycleCheckBox")).click(); 
    assert driver.findElement(By.id("formCourseUnit:cycleCheckBox")).isSelected();
    
    new Select(driver.findElement(By.id("formCourseUnit:turnusCourseUnit"))).selectByVisibleText("Wöchentlich");
    Select selectAfter2 = new Select(driver.findElement(By.id("formCourseUnit:turnusCourseUnit"))); 
    assert selectAfter2.getFirstSelectedOption().getText().equals("Wöchentlich");
    driver.findElement(By.id("formCourseUnit:numberOfCourseUnits")).clear();
    driver.findElement(By.id("formCourseUnit:numberOfCourseUnits")).sendKeys("4");
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
