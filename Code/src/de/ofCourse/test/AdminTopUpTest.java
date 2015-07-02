package de.ofCourse.test;

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Test whether the account balance of an user is charged correctly.
 * 
 * @author Tobias Fuchs
 *
 */
public class AdminTopUpTest {
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
  public void testAdminTopUp() throws Exception {
     driver.get(baseUrl + "OfCourse/");
     driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
     driver.findElement(By.id("formLogin:usernameLogin")).clear();
     driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
     driver.findElement(By.id("formLogin:passwordLogin")).clear();
     driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
     driver.findElement(By.id("formLogin:login")).click();
     driver.findElement(By.linkText("Administration")).click();
     driver.findElement(By.linkText("Seitenverwaltung")).click();
      
    assert driver.findElement(By.id("heading1")).getText().equals("Seitenverwaltung");
    driver.findElement(By.id("formToUpAccount:userIDToTopUp")).clear();
    driver.findElement(By.id("formToUpAccount:userIDToTopUp")).sendKeys("10004");
    driver.findElement(By.id("formToUpAccount:userNameToTopUp")).clear();
    driver.findElement(By.id("formToUpAccount:userNameToTopUp")).sendKeys("Ricky");
    driver.findElement(By.id("formToUpAccount:spendMoney")).click();
    driver.findElement(By.id("formToUpAccount:userNameToTopUp")).clear();
    driver.findElement(By.id("formToUpAccount:userNameToTopUp")).sendKeys("Ricky1");
    driver.findElement(By.id("formToUpAccount:amountToTopUp")).clear();
    driver.findElement(By.id("formToUpAccount:amountToTopUp")).sendKeys("-40.00");
    driver.findElement(By.id("formToUpAccount:spendMoney")).click();
    assert driver.findElement(By.id("messageTopUp")).getText().equals("Der eingegebene Betrag muss positiv sein. Beispiel 10.0");
    driver.findElement(By.id("formToUpAccount:amountToTopUp")).clear();
    driver.findElement(By.id("formToUpAccount:amountToTopUp")).sendKeys("40,00");
    driver.findElement(By.id("formToUpAccount:spendMoney")).click();
    assert driver.findElement(By.id("messageTopUp")).getText().equals("Der eingegebene Betrag entspricht nicht den Vorgaben. Beispiel 10.0");
    driver.findElement(By.id("formToUpAccount:amountToTopUp")).clear();
    driver.findElement(By.id("formToUpAccount:amountToTopUp")).sendKeys("40.00");
    driver.findElement(By.id("formToUpAccount:spendMoney")).click();
    assert driver.findElement(By.id("formToUpAccount:moneyMessage")).getText().equals("Der Account wurde mit 40.0 Euro aufgeladen.");
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
