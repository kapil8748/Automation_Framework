package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtentReportConverter {

    public static void main(String[] args) {
        // Paths matching your GitHub Actions workspace download paths
        String inputArtifactsDir = "build/artifacts";
        String fallbackOutputDir = "build/test-results";
        String reportTargetDestination = "build/reports/extent-dashboard.html";

        ExtentReports extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(reportTargetDestination);
        
        // Customize your corporate framework dashboard look
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Automation Framework - Unified Executive Report");
        spark.config().setReportName("CI/CD Consolidated Automation Run Results");
        extent.attachReporter(spark);

        System.out.println("Processing raw framework outputs for Extent Conversion...");

        // Parse downloaded CI artifacts first, fall back to native folders if local
        parseDirectory(extent, Paths.get(inputArtifactsDir));
        parseDirectory(extent, Paths.get(fallbackOutputDir));

        extent.flush();
        System.out.println("Extent Dashboard successfully compiled at: " + reportTargetDestination);
    }

    private static void parseDirectory(ExtentReports extent, Path basePath) {
        if (!Files.exists(basePath)) return;

        try (Stream<Path> paths = Files.walk(basePath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".xml") && p.getFileName().toString().startsWith("TEST-"))
                 .forEach(path -> {
                     try {
                         parseTestXmlFile(extent, path.toFile());
                     } catch (Exception e) {
                         System.err.println("Could not parse file " + path + ": " + e.getMessage());
                     }
                 });
        } catch (Exception e) {
            System.err.println("Error reading paths directory: " + e.getMessage());
        }
    }

    private static void parseTestXmlFile(ExtentReports extent, File xmlFile) throws Exception {
        String content = new String(Files.readAllBytes(xmlFile.toPath()));
        String suiteName = xmlFile.getName().replace("TEST-", "").replace(".xml", "");
        
        // Robust regex matching BOTH self-closing passing testcases (/>) AND multi-line failure testcases (>(.*?)</testcase>)
        String testcaseRegex = "<testcase\\s+name=\"([^\"]+)\"\\s+classname=\"([^\"]+)\"\\s+time=\"([^\"]+)\"\\s*(?:/>|>(.*?)</testcase>)";
        Matcher matcher = Pattern.compile(testcaseRegex, Pattern.DOTALL).matcher(content);
        
        while (matcher.find()) {
            String testName = matcher.group(1);
            String className = matcher.group(2);
            String duration = matcher.group(3);
            // Group 4 holds the failure block if it exists; it will be null for self-closing passing tags
            String failureBlock = matcher.group(4); 

            ExtentTest extentTest = extent.createTest("[" + suiteName + "] " + className + " -> " + testName);
            extentTest.assignCategory(suiteName);
            extentTest.info("Execution Duration: " + duration + " seconds");

            if (failureBlock != null && failureBlock.contains("<failure")) {
                // Extract error details cleanly
                String errorMessage = failureBlock.replaceAll("<failure[^>]*>", "").replaceAll("</failure>", "").trim();
                extentTest.log(Status.FAIL, "Test Execution Failed!");
                extentTest.fail("<pre>" + errorMessage + "</pre>");
            } else if (failureBlock != null && failureBlock.contains("<skipped")) {
                extentTest.log(Status.SKIP, "Test Case Skipped.");
            } else {
                extentTest.log(Status.PASS, "Passed successfully.");
            }
        }
    }
}