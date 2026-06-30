package base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.WebDriver;

/**
 * BaseTest class
 * Provides basic setup and teardown for test classes.
 * Extend this class in your test cases.
 */
public class BaseTest {

    public static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        // Initialize driver using SeleniumBase
        driver = SeleniumBase.initializeDriver("chrome", false);
        System.out.println("BaseTest: Driver initialized.");
    }

    @AfterAll
    public static void tearDown() {
        // Close driver using SeleniumBase
        SeleniumBase.closeDriver();
        System.out.println("BaseTest: Driver closed.");
    }
}
