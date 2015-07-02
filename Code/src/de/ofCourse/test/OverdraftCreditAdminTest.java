package de.ofCourse.test;

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class OverdraftCreditAdminTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  public static final String messageOverdraftCredit= "messageOverdraftCredit";

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testOverdraftCreditAdmin() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    
    
    
    driver.findElement(By.linkText("Administration")).click();
    driver.findElement(By.linkText("Seitenverwaltung")).click();
    assert driver.findElement(By.id("heading1")).getText().equals("Seitenverwaltung");
    driver.findElement(By.id("formGiveCredit:amountGivenCredit")).clear();
    driver.findElement(By.id("formGiveCredit:amountGivenCredit")).sendKeys("zehn");
    driver.findElement(By.id("formGiveCredit:giveCredit")).click();
    System.out.println(driver.findElement(By.id(messageOverdraftCredit)).getText());
    assert driver.findElement(By.id(messageOverdraftCredit)).getText().equals("Der eingegebene Betrag entspricht nicht den Vorgaben. Beispiel 10.0");
    driver.findElement(By.id("formGiveCredit:amountGivenCredit")).clear();
    driver.findElement(By.id("formGiveCredit:amountGivenCredit")).sendKeys("-10.00");
    driver.findElement(By.id("formGiveCredit:giveCredit")).click();
    assert driver.findElement(By.id(messageOverdraftCredit)).getText().equals("Der eingegebene Betrag muss positiv sein. Beispiel 10.0");
    driver.findElement(By.id("formGiveCredit:amountGivenCredit")).clear();
    driver.findElement(By.id("formGiveCredit:amountGivenCredit")).sendKeys("10.00");
    driver.findElement(By.id("formGiveCredit:giveCredit")).click();
    assert driver.findElement(By.id("formGiveCredit:creditMessage")).getText().equals("Der Ueberziehungskredit wurde festgelegt auf 10.0 Euro.");
   
    
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
