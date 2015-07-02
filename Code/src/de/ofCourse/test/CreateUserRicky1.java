package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class CreateUserRicky1 {
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
  public void testCreateUser() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/authenticate.xhtml");
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
        
    driver.findElement(By.linkText("Administration")).click();
    driver.findElement(By.linkText("Seitenverwaltung")).click();
    driver.findElement(By.id("j_idt54:createNewUser")).click();
    new Select(driver.findElement(By.id("formCreateUser:titleRegister"))).selectByVisibleText("Herr");
    driver.findElement(By.id("formCreateUser:firstnameRegister")).clear();
    driver.findElement(By.id("formCreateUser:firstnameRegister")).sendKeys("Ricky");
    driver.findElement(By.id("formCreateUser:lastnameRegister")).clear();
    driver.findElement(By.id("formCreateUser:lastnameRegister")).sendKeys("Strohmeier");
    driver.findElement(By.id("formCreateUser:birthdateRegister")).clear();
    driver.findElement(By.id("formCreateUser:birthdateRegister")).sendKeys("40.10.1991");
    driver.findElement(By.id("formCreateUser:streetRegister")).clear();
    driver.findElement(By.id("formCreateUser:streetRegister")).sendKeys("Musterstrasse");
    driver.findElement(By.id("formCreateUser:houseNumberRegister")).clear();
    driver.findElement(By.id("formCreateUser:houseNumberRegister")).sendKeys("2");
    driver.findElement(By.id("formCreateUser:locationRegister")).clear();
    driver.findElement(By.id("formCreateUser:locationRegister")).sendKeys("Musterstadt");
    driver.findElement(By.id("formCreateUser:zipcodeRegister")).clear();
    driver.findElement(By.id("formCreateUser:zipcodeRegister")).sendKeys("99999");
    driver.findElement(By.id("formCreateUser:countryRegister")).clear();
    driver.findElement(By.id("formCreateUser:countryRegister")).sendKeys("Deutschland");
    driver.findElement(By.id("formCreateUser:emailRegister")).clear();
    driver.findElement(By.id("formCreateUser:emailRegister")).sendKeys("ricky.strohmeier@gmx.de");
    driver.findElement(By.id("formCreateUser:usernameRegister")).clear();
    driver.findElement(By.id("formCreateUser:usernameRegister")).sendKeys("Ricky1");
    driver.findElement(By.id("formCreateUser:passwordRegister")).clear();
    driver.findElement(By.id("formCreateUser:passwordRegister")).sendKeys("Muhmuh1!");
    driver.findElement(By.id("formCreateUser:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formCreateUser:passwordConfirmRegister")).sendKeys("Muhmuh1!");
    new Select(driver.findElement(By.id("formCreateUser:userRoleSelection"))).selectByVisibleText("Benutzer");
    driver.findElement(By.id("formCreateUser:createNewUser")).click(); 
    driver.findElement(By.id("formCreateUser:birthdateRegister")).clear();
    driver.findElement(By.id("formCreateUser:birthdateRegister")).sendKeys("04.10.1991");
    driver.findElement(By.id("formCreateUser:passwordRegister")).clear();
    driver.findElement(By.id("formCreateUser:passwordRegister")).sendKeys("Muhmuh1!");
    driver.findElement(By.id("formCreateUser:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formCreateUser:passwordConfirmRegister")).sendKeys("Muhmuh1!");
    driver.findElement(By.id("formCreateUser:createNewUser")).click(); 
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
