package testcases;

import base.SeleniumBase;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating SeleniumBase driver initialization
 */
public class SeleniumLoaderTest extends BaseTest {

    @Test
    public void testSeleniumDriverInitialization() {
        
        // 2. Assert: Validate the loader gave us a working engine
        assertNotNull(driver, "WebDriver instance should not be null");

        // 3. Simple action: Navigate to endpoint and fetch the title
        SeleniumBase.getUrl(driver,"https://www.google.com");
        String pageTitle = driver.getTitle();
        System.out.println("Verified Page Target Title: " + pageTitle);

        assertEquals("Google", pageTitle, "");

        // 4. Cleanup: Close driver
        SeleniumBase.closeDriver();
    }
}
