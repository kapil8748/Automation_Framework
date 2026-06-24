package utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class to load .properties files from resources
 */
public class PropertiesLoader {

    /**
     * Load a single properties file from classpath
     */
    public static Properties loadProperties(File file) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            if (input == null) {
                throw new RuntimeException("File not found: " + file.getName());
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Load multiple properties files into a map
     */
    public static Map<String, Properties> loadAll() {
        
       File folder = new File("/home/knfc8748/Automation_Framework/src/main/resources");
        
        System.out.println("Complete Path: "+folder.getAbsolutePath());
         File[] fileNames = folder.listFiles();
        
        Map<String, Properties> propertiesMap = new HashMap<>();
        for (File fileName : fileNames) {
            String key = fileName.getName().replaceFirst("\\.properties$", "");
            propertiesMap.put(key, loadProperties(fileName));
        }
        return propertiesMap;
    }
    
}
