package de.ofCourse.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class DeleteCourseTest {
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
  public void testDeleteCourse() throws Exception {
    driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
    driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
    driver.findElement(By.id("formRegister:usernameRegister")).clear();
    driver.findElement(By.id("formRegister:usernameRegister")).sendKeys("the_muh");
    driver.findElement(By.id("formRegister:passwordRegister")).clear();
    driver.findElement(By.id("formRegister:passwordRegister")).sendKeys("Hallo123!");
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("the_muh");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Hallo123!");
    driver.findElement(By.id("formLogin:usernameLogin")).clear();
    driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
    driver.findElement(By.id("formLogin:passwordLogin")).clear();
    driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
    driver.findElement(By.id("formLogin:login")).click();
    driver.findElement(By.linkText("Administration")).click();
    driver.findElement(By.linkText("Seitenverwaltung")).click();
    driver.findElement(By.id("courseManagement:manageCourses")).click();
    driver.findElement(By.id("formFilterCourses:courseOffers")).click();
    driver.findElement(By.xpath("(//a[contains(text(),'Yoga')])[1]")).click();
    driver.findElement(By.id("courseDetailsID:edit")).click();
    driver.findElement(By.id("courseDetailsID:j_idt94:deleteCourse")).click();
    assertTrue(closeAlertAndGetItsText().matches("^Kurs wirklich löschen[\\s\\S]$"));
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
