package de.ofCourse.test;

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AdaptedEditUnitTest {
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
    public void testAdaptedEditUnit() throws Exception {
	driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
	driver.findElement(By.id("generalNavigationForm:authenticateLink"))
		.click();
	driver.findElement(By.id("formLogin:usernameLogin")).clear();
	driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("Basti3");
	driver.findElement(By.id("formLogin:passwordLogin")).clear();
	driver.findElement(By.id("formLogin:passwordLogin")).sendKeys(
		"Basti#249");
	driver.findElement(By.id("formLogin:login")).click();
	driver.findElement(By.id("leadedCoursesList:courseTable:0:details"))
		.click();
	driver.findElement(By.id("j_idt110:j_idt112:3:bearbeiten")).click();

	assert driver.findElement(By.id("headingEditUnit")).getText()
		.equals("Kurseinheit bearbeiten");
	driver.findElement(By.id("formCourseUnit:dateCourseUnit")).clear();
	driver.findElement(By.id("formCourseUnit:dateCourseUnit")).sendKeys(
		"1.06.2015");
	driver.findElement(By.id("formCourseUnit:saveCourseUnit")).click();
	assert driver
		.findElement(By.id("globalMessages"))
		.getText()
		.equals("Die Kurseinheit liegt nicht im Bereich des Kurses vom 27.7.2015 bis zum 27.7.2016 !");
	driver.findElement(By.id("formCourseUnit:dateCourseUnit")).clear();
	driver.findElement(By.id("formCourseUnit:dateCourseUnit")).sendKeys(
		"5.13.2015");
	driver.findElement(By.id("formCourseUnit:saveCourseUnit")).click();

	assert driver
		.findElement(By.id("dateCourseUnitMessage"))
		.getText()
		.equals("Datum existiert nicht oder Format (dd.MM.yyyy) ist falsch.");
	driver.findElement(By.id("formCourseUnit:dateCourseUnit")).clear();
	driver.findElement(By.id("formCourseUnit:dateCourseUnit")).sendKeys(
		"4.8.2015");
	driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit")).clear();
	driver.findElement(By.id("formCourseUnit:maxUsersCourseUnit"))
		.sendKeys("20");
	driver.findElement(By.id("formCourseUnit:saveCourseUnit")).click();
	assert driver.findElement(By.id("courseDetailTitle")).getText()
		.equals("Kursdetails");

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
