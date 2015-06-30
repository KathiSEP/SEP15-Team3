package de.ofCourse.test;

/**
 * Testing of create course. This test is geared to the tests T10-80 and T10-90
 * from our product brief. Furthermore there are a view more tests for the 
 * faults that can appear because of invalid user inserts. 
 * The two courses 'Yoga' and 'Standardtanz' will be created in this test. On
 * top of that it is asserted that the faces messages to the user insert are 
 * correct and that the user will be sent up to the right page.
 * 
 * @author Katharina Hölzl
 */
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class CreateCourseTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  
  public static final String messageCourseTitle= "messageCourseTitle";
  public static final String messageCourseLeaderID= "messageCourseLeaderID";
  public static final String messageCourseParticipants= "messageCourseParticipants";
  public static final String messageStartDate= "messageStartDate";
  public static final String messageEndDate= "messageEndDate";
  public static final String messageImage= "messageImage";
  

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8003/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testCreateCourse() throws Exception {

      driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
      // Login as administrator and navigate to the page createCourse
      driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
      assert driver.findElement(By.id("authenticate")).getText().equals("Anmeldung");
      
      driver.findElement(By.id("formLogin:usernameLogin")).clear();
      driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("admin1");
      driver.findElement(By.id("formLogin:passwordLogin")).clear();
      driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Password!123");
      driver.findElement(By.id("formLogin:login")).click();
      driver.findElement(By.linkText("Administration")).click();
      driver.findElement(By.linkText("Seitenverwaltung")).click();
      assert driver.findElement(By.id("heading1")).getText().equals("Seitenverwaltung");
      
      driver.findElement(By.id("courseManagement:createNewCourse")).click();
      assert driver.findElement(By.id("createCourse")).getText().equals("Neuen Kurs anlegen");
      
      // Testing required messages 
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageCourseTitle)).getText().contains("Bitte geben Sie einen Titel ein. ");
      assert driver.findElement(By.id(messageCourseLeaderID)).getText().contains("Bitte KursleiterID eingeben. ");
      assert driver.findElement(By.id(messageStartDate)).getText().contains("Bitte Startdatum eingeben. ");
      assert driver.findElement(By.id(messageEndDate)).getText().contains("Bitte Enddatum eingeben. ");
      
      // Testing insert negative numbers at course leader ID and max. participants
      driver.findElement(By.id("formCreateCourse:courseTitle")).clear();
      driver.findElement(By.id("formCreateCourse:courseTitle")).sendKeys("Yoga");
      driver.findElement(By.id("formCreateCourse:courseDescription")).clear();
      driver.findElement(By.id("formCreateCourse:courseDescription")).sendKeys("Bringe Koerper, Geist und Seele in Einklang durch Yoga Uebungen. Jeder Teilnehmer sollte seine eigene Turnmatte mitnehmen");
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).clear();
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).sendKeys("-12");
      driver.findElement(By.id("formCreateCourse:courseParticipants")).clear();
      driver.findElement(By.id("formCreateCourse:courseParticipants")).sendKeys("-12");
      driver.findElement(By.id("formCreateCourse:courseStartDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseStartDate")).sendKeys("12.03.2015");
      driver.findElement(By.id("formCreateCourse:courseEndDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseEndDate")).sendKeys("12.03.2016");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageCourseLeaderID)).getText().contains("Bitte geben Sie eine positive Zahl als KursleiterID ein.");
      assert driver.findElement(By.id(messageCourseParticipants)).getText().contains("Bitte positive Zahl eingeben. ");
      
      // Testing course leader id does not exist
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).clear();
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).sendKeys("123456");
      driver.findElement(By.id("formCreateCourse:courseParticipants")).clear();
      driver.findElement(By.id("formCreateCourse:courseParticipants")).sendKeys("20");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageCourseLeaderID)).getText().contains("Die eingegebene Kursleiter-ID existiert nicht im System. ");
      
      // Testing end date does not exist and start date has a invalid format
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).clear();
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).sendKeys("10027");
      driver.findElement(By.id("formCreateCourse:courseStartDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseStartDate")).sendKeys("123");
      driver.findElement(By.id("formCreateCourse:courseEndDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseEndDate")).sendKeys("33.03.2016");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageStartDate)).getText().contains("Datum existiert nicht oder Format (dd.MM.yyyy) ist falsch. ");
      assert driver.findElement(By.id(messageEndDate)).getText().contains("Datum existiert nicht oder Format (dd.MM.yyyy) ist falsch. ");
      
      // Testing end date is before the start date
      driver.findElement(By.id("formCreateCourse:courseStartDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseStartDate")).sendKeys("12.03.2015");
      driver.findElement(By.id("formCreateCourse:courseEndDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseEndDate")).sendKeys("03.02.2015");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageStartDate)).getText().contains("Startdatum muss vor dem Enddatum liegen. ");
      
      // Testing wrong image format
      driver.findElement(By.id("formCreateCourse:courseEndDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseEndDate")).sendKeys("03.02.2016");
      driver.findElement(By.id("formCreateCourse:courseImage")).clear();
      driver.findElement(By.id("formCreateCourse:courseImage")).sendKeys("C:\\Users\\Kathi\\Desktop\\url.png");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageImage)).getText().contains("Kein gültiges Bildformat. ");
      
      //Testing wrong image size
      driver.findElement(By.id("formCreateCourse:courseImage")).clear();
      driver.findElement(By.id("formCreateCourse:courseImage")).sendKeys("C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      
      assert driver.findElement(By.id(messageImage)).getText().contains("Das Bild muss kleiner als 100 KB sein. ");
      
      // Testing correct data for the course 'Yoga'
      driver.findElement(By.id("formCreateCourse:courseImage")).clear();
      driver.findElement(By.id("formCreateCourse:courseImage")).sendKeys("C:\\Users\\Kathi\\Desktop\\domo.jpg");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Yoga");
      
      // Testing create a new course 'Standardtanz'
      driver.findElement(By.linkText("Administration")).click();
      driver.findElement(By.linkText("Seitenverwaltung")).click();
      assert driver.findElement(By.id("heading1")).getText().equals("Seitenverwaltung");
      
      driver.findElement(By.id("courseManagement:createNewCourse")).click();   
      assert driver.findElement(By.id("createCourse")).getText().equals("Neuen Kurs anlegen");
      
      driver.findElement(By.id("formCreateCourse:courseTitle")).clear();
      driver.findElement(By.id("formCreateCourse:courseTitle")).sendKeys("Standardtanz");
      driver.findElement(By.id("formCreateCourse:courseDescription")).clear();
      driver.findElement(By.id("formCreateCourse:courseDescription")).sendKeys("Grundkurs Standardtanz. Erlernt werden Taenze wie Walzer, DiscoFox, Cha-Cha-Cha, SlowFox und Tango. Die Teilnahme als Paar ist wuenschenswert.");
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).clear();
      driver.findElement(By.id("formCreateCourse:IdOfCourseLeader")).sendKeys("10027");
      driver.findElement(By.id("formCreateCourse:courseParticipants")).clear();
      driver.findElement(By.id("formCreateCourse:courseParticipants")).sendKeys("30");
      driver.findElement(By.id("formCreateCourse:courseStartDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseStartDate")).sendKeys("04.08.2015");
      driver.findElement(By.id("formCreateCourse:courseEndDate")).clear();
      driver.findElement(By.id("formCreateCourse:courseEndDate")).sendKeys("30.09.2015");
      driver.findElement(By.id("formCreateCourse:createNewCourse")).click();
      assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Standardtanz");
      
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
