package de.ofCourse.test;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class AccountActivationTypeAdminTest {
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
  public void testAccountActivationTypeAdmin() throws Exception {
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
    Select selectBefore = new Select(driver.findElement(By.id("formActivationType:accountActivationSelection"))); 
    assert selectBefore.getFirstSelectedOption().getText().equals("E-Mail-Verifikation");
    new Select(driver.findElement(By.id("formActivationType:accountActivationSelection"))).selectByVisibleText("E-Mail-Verifikation und Aktivierung durch Administrator");
    Select selectAfter = new Select(driver.findElement(By.id("formActivationType:accountActivationSelection"))); 
    assert selectAfter.getFirstSelectedOption().getText().equals("E-Mail-Verifikation und Aktivierung durch Administrator");
    driver.findElement(By.id("formActivationType:saveAccountActivation")).click();
    assert driver.findElement(By.id("formActivationType:accountActivationMessage")).getText().equals("Der Aktivierungstyp wurde erfolgreich geaendert.");
    
    
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
