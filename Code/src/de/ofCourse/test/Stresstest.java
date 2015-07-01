package de.ofCourse.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
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

public class Stresstest {
    private String baseUrl;

    private List<Thread> threadList = new ArrayList<Thread>();
    private static final int threadNumber = 2;

    private boolean start = false;

    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    public static final String facesMessages = "facesMessages";

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < threadNumber; i++) {
            Thread thread = new Thread(new DriverThread());
            threadList.add(thread);
        }
        
        baseUrl = "http://localhost:8003/";
    }

    @Test
    public void stresstest() throws Exception {
        for (int i = 0; i < threadNumber; i++) {
            threadList.get(i).start();
        }

        Thread.sleep(threadNumber * 5000);

        start = true;

        while(isThreadRunning() == true) {
            Thread.sleep(1000);
        }
    }
    
    private boolean isThreadRunning() {
        boolean running = false;
        for(Thread thread : threadList) {
            if(thread.isAlive() == true) {
                running = true;
                break;
            }
        }
        return running;
    }

    private class DriverThread implements Runnable {
        WebDriver driver;

        @Override
        public void run() {
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            baseUrl = "http://localhost:8003/";
            driver.get(baseUrl + "OfCourse/facelets/open/index.xhtml");

            while (start == false) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }

            driver.findElement(By.id("generalNavigationForm:authenticateLink"))
                    .click();
            assert driver.findElement(By.id("authenticate")).getText()
                    .equals("Anmeldung");

            driver.findElement(By.id("formLogin:usernameLogin")).clear();
            driver.findElement(By.id("formLogin:usernameLogin")).sendKeys(
                    "admin1");
            driver.findElement(By.id("formLogin:passwordLogin")).clear();
            driver.findElement(By.id("formLogin:passwordLogin")).sendKeys(
                    "Password!123");
            driver.findElement(By.id("formLogin:login")).click();
            driver.findElement(By.linkText("Suche")).click();
            driver.findElement(By.id("formFilterCourses:courseOffers")).click();
            driver.findElement(By.linkText("Zweiter Test")).click();
            assert driver.findElement(By.id("courseDetailTitle")).getText()
                    .equals("Zweiter Test");
            driver.quit();
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

    @After
    public void tearDown() throws Exception {
        for (Thread thread : threadList) {
            thread.interrupt();
        }
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}
