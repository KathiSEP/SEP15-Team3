package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class AddLeaderToCourseTest {
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

  //TODO auf Facesmessages pr�fen !!
  
  @Test
  public void testAddLeaderToCourse() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Suche")).click();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    driver.findElement(By.linkText("Zweiter Test")).click();
    driver.findElement(By.name("courseDetailsID:j_idt84")).click();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("-1234");
    driver.findElement(By.name("courseDetailsID:j_idt106")).click();
    driver.findElement(By.name("courseDetailsID:j_idt84")).click();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("123456");
    driver.findElement(By.name("courseDetailsID:j_idt106")).click();
    driver.findElement(By.name("courseDetailsID:j_idt84")).click();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).clear();
    driver.findElement(By.id("courseDetailsID:leaderIDField")).sendKeys("10027");
    driver.findElement(By.name("courseDetailsID:j_idt106")).click();
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
