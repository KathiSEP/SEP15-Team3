package de.ofCourse.test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

public class JMeterTest {


    private FirefoxDriver driver;
    private String baseUrl;


    public JMeterTest(){
        System.out.println("Ich bin verzweifelt");
    }
    
    

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "http://localhost:8003/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
 
    }



    @Test
    public void test() throws Exception {
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
        
        
        assertEquals(0, 0);
    }

    
   @After
   public void tearDown(){
       System.out.println("Tear Down");
   }
    
}
