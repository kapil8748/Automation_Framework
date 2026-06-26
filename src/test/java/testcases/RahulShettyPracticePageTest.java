package testcases;

// Framework Base and Utility Imports
import base.BaseTest;
import base.SeleniumBase;
import utils.PropertiesLoader;

// Java Core Imports
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait; 
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import java.time.Duration;


public class RahulShettyPracticePageTest extends BaseTest{
        
        Map<String, Properties> myProps = PropertiesLoader.loadAll();
        String url2= myProps.get("RahulShettyPracticePage").getProperty("url");

    @Test
    public void Radio(){
        
        SeleniumBase.getUrl(driver,url2);
        WebElement radio1 = driver.findElement(By.xpath("//input[@value='radio1']"));
        radio1.click();
        Assertions.assertTrue(radio1.isSelected());
        
    }

    @Test
    public void CheckBox(){
        SeleniumBase.getUrl(driver,url2);
        WebElement checkbox1 = driver.findElement(By.id("checkBoxOption1"));
        checkbox1.click();
        Assertions.assertTrue(checkbox1.isSelected());

    }
    
    @Test
    public void DropDown(){
        SeleniumBase.getUrl(driver,url2);
        WebElement dropdown = driver.findElement(By.id("dropdown-class-example"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Option2");
        Assertions.assertEquals("option2", select.getFirstSelectedOption().getAttribute("value"));
    }
     @Test
    public void AlertBtn(){
            SeleniumBase.getUrl(driver,url2);
            driver.findElement(By.id("alertbtn")).click();
            Alert alert = driver.switchTo().alert();
            Assertions.assertEquals("Hello , share this practice page and share your knowledge", alert.getText());
            alert.accept();
    }

    @Test
    public void ConfirmBtn(){
        SeleniumBase.getUrl(driver,url2);
        driver.findElement(By.id("confirmbtn")).click();
        Alert confirm = driver.switchTo().alert();
        Assertions.assertEquals("Hello , Are you sure you want to confirm?", confirm.getText());
        confirm.dismiss();
        
    }
    @Test
    public void MouseHover(){
        SeleniumBase.getUrl(driver,url2);
        WebElement hoverButton = driver.findElement(By.id("mousehover"));
        Actions actions = new Actions(driver);
        actions.moveToElement(hoverButton).perform();
        WebElement topLink = driver.findElement(By.xpath("//div[@class='mouse-hover-content']//a[text()='Top']"));
        Assertions.assertTrue(topLink.isDisplayed());
        
    }
    @Test
    public void Iframes(){
        SeleniumBase.getUrl(driver,url2);
        driver.switchTo().frame("courses-iframe");
        WebElement elementInsideFrame = driver.findElement(By.xpath("//a[contains(@href, 'courses')]"));
        Assertions.assertTrue(elementInsideFrame.isDisplayed());
    
        driver.switchTo().defaultContent();
    }

    @Test
    public void AutoComplete(){
        SeleniumBase.getUrl(driver,url2);
        WebElement autocomplete = driver.findElement(By.id("autocomplete"));
        autocomplete.sendKeys("Ind");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement suggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//li/div[text()='India']")
        ));
        
        suggestion.click();
        Assertions.assertEquals("India", autocomplete.getAttribute("value"));
    }
    
    @Test
    public void ElementShowBtn(){
        SeleniumBase.getUrl(driver,url2);
        WebElement textBox = driver.findElement(By.id("displayed-text"));
        driver.findElement(By.id("hide-textbox")).click();
        Assertions.assertFalse(textBox.isDisplayed());
        driver.findElement(By.id("show-textbox")).click();
        Assertions.assertTrue(textBox.isDisplayed());
    }
    
}


