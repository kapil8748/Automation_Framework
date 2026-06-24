package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to load and parse JSON test data from resources.
 */
public class JsonLoader {

    public static List<String> loadJson() {
        List<String> tokens = new ArrayList<>();

        try {
            // Load data.json from classpath (src/main/resources)
            InputStream is = JsonLoader.class.getClassLoader().getResourceAsStream("data.json");

            if (is == null) {
                throw new RuntimeException("data.json not found in resources folder");
            }

            // Parse JSON using Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            // Iterate over all fields and values
            Iterator<String> fieldNames = root.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                tokens.add(field); // add key
                tokens.add(root.get(field).asText()); // add value
            }

        } catch (Exception e) {
            System.err.println("ERROR: Failed to read or parse JSON file: " + e.getMessage());
            e.printStackTrace();
        }

        return tokens;
    }
}
