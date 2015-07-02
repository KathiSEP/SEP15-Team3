package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.reporters.jq.TimesPanel;

public class TestMeasurment {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://muhingen.ddns.net/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testMeasurment() throws Exception {
    driver.get(baseUrl + "OfCourse");
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("blacky");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Test!1990");
    long start1 = System.currentTimeMillis();
    driver.findElement(By.id("formLogin:login")).click();
    long end1 = System.currentTimeMillis();
    
    driver.findElement(By.linkText("Suche")).click();
    
    
    long start2 = System.currentTimeMillis();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    long end2 = System.currentTimeMillis();
    
    
    long start3 = System.currentTimeMillis();
    driver.findElement(By.linkText("Zweiter Test")).click();
    driver.findElement(By.id("j_idt110:j_idt112:0:anmelden")).click();
    driver.findElement(By.id("j_idt110:j_idt112:2:anmelden")).click();
    driver.findElement(By.id("j_idt110:j_idt112:0:abmelden")).click();
    driver.findElement(By.id("j_idt110:j_idt112:2:abmelden")).click();
    long end3 = System.currentTimeMillis();
    
    
    
    driver.findElement(By.linkText("Suche")).click();
    driver.findElement(By.id("formFilterCourses:filterInput")).clear();
    driver.findElement(By.id("formFilterCourses:filterInput")).sendKeys("Yoga");
    
    long start4 = System.currentTimeMillis();
    driver.findElement(By.id("formFilterCourses:searchCourses")).click();
    driver.findElement(By.linkText("Yoga")).click();
    long end4 = System.currentTimeMillis();
    
    
    
    long start5 = System.currentTimeMillis();
    driver.findElement(By.linkText("Administration")).click();
    driver.findElement(By.linkText("Seitenverwaltung")).click();
    driver.findElement(By.id("j_idt54:manageUsers")).click();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    long end5 = System.currentTimeMillis();
    
    System.out.println("Login took:" + (end1 - start1) + "ms");
    System.out.println("Search All took:" + (end2 - start2) + "ms");
    System.out.println("SignUp for CourseUnits took:" + (end3 - start3) + "ms");
    System.out.println("SearchSpecificCourse took:" + (end4 - start4) + "ms");
    System.out.println("Search Users took:" + (end5 - start5) + "ms");
    
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
