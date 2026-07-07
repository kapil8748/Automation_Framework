package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.MediaEntityBuilder;
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
        
        // 1. Customize your corporate framework dashboard look
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Your Company Name - Unified Executive Report");
        spark.config().setReportName("CI/CD Consolidated Automation Run Results");

        // 2. Inject CSS to add your Company Name and Company Logo to the brand sidebar area
        String customCss = 
            ".nav-logo { " +
            "   background-image: url('https://yourcompany.com/path-to-logo.png') !important; " + // Update with your actual logo URL
            "   background-size: contain !important; " +
            "   background-repeat: no-repeat !important; " +
            "   height: 35px !important; " +
            "   width: 35px !important; " +
            "   margin: 10px !important; " +
            "} " +
            ".brand-logo { font-weight: bold !important; font-size: 15px !important; padding-left: 5px !important; }";
        spark.config().setCss(customCss);

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

                // 3. Handle Failure Screenshot Attachment dynamically
                // Looks for files matching the testName layout (e.g., testName.png) inside your screenshots directory
                String screenshotFilename = testName + ".png";
                File screenshotFile = new File("build/reports/screenshots/" + screenshotFilename);

                if (screenshotFile.exists()) {
                    // Use relative pathing from build/reports/extent-dashboard.html to build/reports/screenshots/
                    String relativeReportPath = "screenshots/" + screenshotFilename;
                    extentTest.fail("Failure Screen Capture:", 
                        MediaEntityBuilder.createScreenCaptureFromPath(relativeReportPath).build());
                } else {
                    extentTest.info("No corresponding failure screenshot found at: build/reports/screenshots/" + screenshotFilename);
                }

            } else if (failureBlock != null && failureBlock.contains("<skipped")) {
                extentTest.log(Status.SKIP, "Test Case Skipped.");
            } else {
                extentTest.log(Status.PASS, "Passed successfully.");
            }
        }
    }
}