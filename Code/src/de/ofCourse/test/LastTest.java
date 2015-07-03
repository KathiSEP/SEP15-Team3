package de.ofCourse.test;

import java.util.concurrent.TimeUnit;


import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;




import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LastTest {
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
  public void testLast() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    Thread.sleep(1000);
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("Basti20");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Lasttest!1");
    Thread.sleep(2000);
    
    driver.findElement(By.id("formLogin:login")).click();
    
    
    
    for (int i = 0; i<=50; i++){
        Thread.sleep(1000);
        driver.findElement(By.linkText("Suche")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("formFilterCourses:courseOffers")).click();
        Thread.sleep(2000);
        driver.findElement(By.linkText("Zweiter Test")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt98")).click();
        driver.findElement(By.id("j_idt110:j_idt112:0:anmelden")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("j_idt110:j_idt112:2:anmelden")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("j_idt110:j_idt112:0:abmelden")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("j_idt110:j_idt112:2:abmelden")).click();
        Thread.sleep(2000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt100")).click();
        Thread.sleep(2000);
        driver.findElement(By.linkText("Suche")).click();
        driver.findElement(By.id("formFilterCourses:filterInput")).clear();
        driver.findElement(By.id("formFilterCourses:filterInput")).sendKeys("Yoga");
        Thread.sleep(2000);
        driver.findElement(By.id("formFilterCourses:searchCourses")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("Yoga")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt98")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt100")).click();
        Thread.sleep(1000);
        System.out.println("Loop " + i);  
    }
    
    // ERROR: Caught exception [ERROR: Unsupported command [getEval | ${i}+1 | ]]
    // ERROR: Caught exception [unknown command [endWhile]]
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
