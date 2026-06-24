package pages;

import base.SeleniumBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Page Object for Login Page
 */
public class LoginPage {

    // ===== Page Actions =====
    public static void enterUsername(WebDriver driver,String username) {
        WebElement userInput = driver.findElement(By.id("login_field"));
        userInput.clear();
        userInput.sendKeys(username);
    }

    public static void enterPassword(WebDriver driver,String password) {
        WebElement passInput = driver.findElement(By.id("password"));
        passInput.clear();
        passInput.sendKeys(password);
    }

    public static void clickLogin(WebDriver driver) {
        driver.findElement(By.name("commit")).click();
    }


    /**
     * Combined login method
     */
    public static void login( WebDriver driver,String username, String password) {
        enterUsername(driver,username);
        enterPassword(driver,password);
        clickLogin(driver);
        System.out.println("Login attempted on GitHub!");
    }
}
