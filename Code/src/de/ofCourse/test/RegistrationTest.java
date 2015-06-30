package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class RegistrationTest {
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
  public void testRegistration() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    
    // Testing required messages
    new Select(driver.findElement(By.id("formRegister:titleRegister"))).selectByVisibleText("Frau");
    driver.findElement(By.id("formRegister:firstnameRegister")).clear();
    driver.findElement(By.id("formRegister:firstnameRegister")).sendKeys("Katharina");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing user name is already existing
    driver.findElement(By.id("formRegister:lastnameRegister")).clear();
    driver.findElement(By.id("formRegister:lastnameRegister")).sendKeys("Hoelzl");
    driver.findElement(By.id("formRegister:usernameRegister")).clear();
    driver.findElement(By.id("formRegister:usernameRegister")).sendKeys("admin1");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("blablabal");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("blablabal");
    driver.findElement(By.id("formRegister:locationRegister")).clear();
    driver.findElement(By.id("formRegister:locationRegister")).sendKeys("Vilshofen");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("94474");
    driver.findElement(By.id("formRegister:countryRegister")).clear();
    driver.findElement(By.id("formRegister:countryRegister")).sendKeys("Deutschland");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina_hoelzl@web.de");
    driver.findElement(By.id("formRegister:selectAGB")).click();
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing AGB not accepted
    driver.findElement(By.id("formRegister:usernameRegister")).clear();
    driver.findElement(By.id("formRegister:usernameRegister")).sendKeys("Kathi6");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing no passwords inserted
    driver.findElement(By.id("formRegister:selectAGB")).click();
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing mail is already existing
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing wrong mail format
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina_hoelzl");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing wrong insert format (no number)
    driver.findElement(By.id("formRegister:emailRegister")).clear();
    driver.findElement(By.id("formRegister:emailRegister")).sendKeys("katharina.hoelzl934@gmx.de");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:birthdateRegister")).clear();
    driver.findElement(By.id("formRegister:birthdateRegister")).sendKeys("test");
    driver.findElement(By.id("formRegister:houseNumberRegister")).clear();
    driver.findElement(By.id("formRegister:houseNumberRegister")).sendKeys("test");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("test");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing date of birth is in the future
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:birthdateRegister")).clear();
    driver.findElement(By.id("formRegister:birthdateRegister")).sendKeys("29.05.2016");
    driver.findElement(By.id("formRegister:houseNumberRegister")).clear();
    driver.findElement(By.id("formRegister:houseNumberRegister")).sendKeys("");
    driver.findElement(By.id("formRegister:locationRegister")).clear();
    driver.findElement(By.id("formRegister:locationRegister")).sendKeys("Engertsham");
    driver.findElement(By.id("formRegister:zipcodeRegister")).clear();
    driver.findElement(By.id("formRegister:zipcodeRegister")).sendKeys("94081");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing passwords not equal
    driver.findElement(By.id("formRegister:birthdateRegister")).clear();
    driver.findElement(By.id("formRegister:birthdateRegister")).sendKeys("29.05.1993");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8");
    driver.findElement(By.id("formRegister:register")).click();
    
    // Testing correct insert of all data
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:streetRegister")).clear();
    driver.findElement(By.id("formRegister:streetRegister")).sendKeys("Am Kastenfeld");
    driver.findElement(By.id("formRegister:houseNumberRegister")).clear();
    driver.findElement(By.id("formRegister:houseNumberRegister")).sendKeys("39");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).clear();
    driver.findElement(By.id("formRegister:passwordConfirmRegister")).sendKeys("bSdFg7HjK8*");
    driver.findElement(By.id("formRegister:selectAGB")).click();
    driver.findElement(By.id("formRegister:register")).click();
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
