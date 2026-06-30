package base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * JUnit 5 base class for all REST API test classes.
 * Mirrors BaseTest.java on the Selenium side.
 */
public class RestApiBaseTest extends RestApiBase {

    @BeforeAll
    static void beforeAll() {
        setupApi();
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[RestApiBaseTest] Suite complete. Base URL: " + baseUrl);
    }
}
