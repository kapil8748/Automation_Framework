package testcases;
import base.SeleniumBase;
import base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import utils.PropertiesLoader;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;



@Tag("Test_First_Test")
public class TestFirstTest extends BaseTest {
    
    
    @Test
    public void loadUrl(){
        Map<String, Properties> myProps = PropertiesLoader.loadAll();
        assertNotNull(myProps, "Properties map should not be null");
        //assertTrue(myProps.containsKey("test1"), "test1 should be loaded");
        String url= myProps.get("test1").getProperty("url");
 
        assertNotNull(driver, "WebDriver instance should not be null");
        SeleniumBase.getUrl(driver,url);
    }
}
