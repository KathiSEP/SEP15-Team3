package de.ofCourse.test;

import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Tests whether a regular course unit is deleted correctly.
 * 
 * @author Tobias Fuchs
 *
 */
public class AdaptedDeleteUnitTest {
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
    public void testAdaptedDeleteUnit() throws Exception {
	driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");
	driver.findElement(By.id("generalNavigationForm:authenticateLink")).click();
	driver.findElement(By.id("formLogin:usernameLogin")).clear();
	driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("Basti3");
	driver.findElement(By.id("formLogin:passwordLogin")).clear();
	driver.findElement(By.id("formLogin:passwordLogin")).sendKeys("Muhmuh1!");
	driver.findElement(By.id("formLogin:login")).click();
	driver.findElement(By.id("leadedCoursesList:courseTable:1:details")).click();

	assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Kursdetails");
	driver.findElement(By.id("j_idt113:j_idt115:1:bearbeiten")).click();

	Select select = new Select(driver.findElement(By
		.id("formCourseUnit:messageCourseUnit")));
	assert select.getFirstSelectedOption().getText().equals("Niemand");

	driver.findElement(By.id("formCourseUnit:completeCycle")).click();
	assert driver.findElement(By.id("formCourseUnit:completeCycle")).isSelected();

	driver.findElement(By.id("formCourseUnit:deleteCourseUnit")).click();
	assertTrue(closeAlertAndGetItsText().matches(
		"^Wollen Sie die Kurseinheit/en wirklich loeschen[\\s\\S]$"));

	assert driver.findElement(By.id("courseDetailTitle")).getText().equals("Kursdetails");
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
