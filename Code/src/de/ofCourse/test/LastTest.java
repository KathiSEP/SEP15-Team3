/**
 * 
 */
package de.ofCourse.test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author Sebastian Schwarz
 *
 */
public class LastTest {

    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    public LastTest(){
        System.out.println("TestEinfachMal");
    }
    
    
    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "http://localhost:8003/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void lasttest() throws Exception {
        driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");

        driver.findElement(By.id("generalNavigationForm:authenticateLink"))
                .click();
        Thread.sleep(1000);
        driver.findElement(By.id("formLogin:usernameLogin")).clear();
        driver.findElement(By.id("formLogin:usernameLogin")).sendKeys("blacky");
        driver.findElement(By.id("formLogin:passwordLogin")).clear();
        driver.findElement(By.id("formLogin:passwordLogin")).sendKeys(
                "Test!1990");
        Thread.sleep(2000);

        driver.findElement(By.id("formLogin:login")).click();

        Thread.sleep(1000);
        driver.findElement(By.linkText("Suche")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("formFilterCourses:courseOffers")).click();
        Thread.sleep(2000);
        driver.findElement(By.linkText("Zweiter Test")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt98")).click();
        driver.findElement(By.id("j_idt110:j_idt112:0:anmelden")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("j_idt110:j_idt112:2:anmelden")).click();
        Thread.sleep(2000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt100")).click();
        Thread.sleep(2000);
        driver.findElement(By.linkText("Suche")).click();
        driver.findElement(By.id("formFilterCourses:filterInput")).clear();
        driver.findElement(By.id("formFilterCourses:filterInput")).sendKeys(
                "Yoga");
        Thread.sleep(2000);
        driver.findElement(By.id("formFilterCourses:searchCourses")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("Yoga")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt98")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("courseDetailsID:j_idt94:j_idt100")).click();
        Thread.sleep(1000);
        

        
        driver.findElement(By.id("generalNavigationForm:logoutLink")).click();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    @SuppressWarnings("unused")
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    @SuppressWarnings("unused")
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
