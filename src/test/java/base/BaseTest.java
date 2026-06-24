package base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;

/**
 * BaseTest class
 * Provides basic setup and teardown for test classes.
 * Extend this class in your test cases.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Initialize driver using SeleniumBase
        driver = SeleniumBase.initializeDriver("chrome", false);
        System.out.println("BaseTest: Driver initialized.");
    }

    @AfterEach
    public void tearDown() {
        // Close driver using SeleniumBase
        SeleniumBase.closeDriver();
        System.out.println("BaseTest: Driver closed.");
    }
}
