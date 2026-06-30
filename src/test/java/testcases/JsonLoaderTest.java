package testcases;

import base.BaseTest;
import org.junit.jupiter.api.*;
import utils.JsonLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating JsonLoader functionality
 */
@Tag("Json_Loader_Test")
public class JsonLoaderTest extends BaseTest {

    @Test
    public void testNativeJsonTokenizer() {
        // 1. Act: Run your native JSON parser
        List<String> tokens = JsonLoader.loadJson();

        // 2. Assert: Verify the behavior automatically
        assertNotNull(tokens, "The token list should not be null");
        assertFalse(tokens.isEmpty(), "The token list should not be empty. Check if data.json exists and contains elements!");

        // Example validation: check if a known token exists
        assertTrue(tokens.contains("username"), "JSON should contain 'username' field");
        assertTrue(tokens.contains("password"), "JSON should contain 'password' field");

        // Print the extracted elements out to console using your clean Gradle stream log setup
        System.out.println("--- Extracted Tokens Natively In Test ---");
        tokens.forEach(token -> System.out.println("Data Node: " + token));
    }
}
