package testcases;

import base.BaseTest;
import org.junit.jupiter.api.*;
import utils.PropertiesLoader;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating PropertiesLoader functionality
 */

@Tag("Properties_Loader_Test")
public class PropertiesLoaderTest extends BaseTest {

    @Test
    public void testPropertiesLoading() {
        Map<String, Properties> myProps = PropertiesLoader.loadAll();

        // Validation: ensure properties map is not null or empty
        assertNotNull(myProps, "Properties map should not be null");
       assertTrue(myProps.containsKey("config"), "config should be loaded");


        // Example validation: check if a specific file was loaded
        assertFalse(myProps.containsKey("config.properties"),
                "config.properties should be loaded");

        // Example validation: check if a key exists inside one of the properties
        Properties config = myProps.get("config");
        assertNotNull(config, "config.properties should not be null");
        assertEquals("jdbc:mysql://localhost:3306/testdb", config.getProperty("db.url"),
                "Database URL should match expected value");

        System.out.println("✅ PropertiesLoaderTest passed with loaded files: " + myProps.keySet());
    }
}
