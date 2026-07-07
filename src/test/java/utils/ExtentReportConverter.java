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
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtentReportConverter {

    public static void main(String[] args) {
        String inputArtifactsDir = "build/artifacts";
        String fallbackOutputDir = "build/test-results";
        String reportTargetDestination = "build/reports/extent-dashboard.html";

        // Create the directory if it doesn't exist
        File reportDir = new File("build/reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // --- FIXED FOR GITHUB ACTIONS PERMANENT STORAGE ---
        // Looks for the permanent asset in your project repository resources first
        try {
            Path sourceLogo = Paths.get("src/test/resources/download.jpeg");
            // Fallback to root if you decide to keep it in the root folder of your repository
            if (!Files.exists(sourceLogo)) {
                sourceLogo = Paths.get("download.jpeg");
            }
            
            Path targetLogo = Paths.get("build/reports/download.jpeg");
            
            if (Files.exists(sourceLogo)) {
                Files.copy(sourceLogo, targetLogo, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Logo cloned from repository resources into report directory successfully.");
            } else {
                System.out.println("Warning: download.jpeg asset missing from repository workspace.");
            }
        } catch (Exception e) {
            System.err.println("Failed to transfer logo asset: " + e.getMessage());
        }

        ExtentReports extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(reportTargetDestination);
        
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("SpaceX Automation Framework - Unified Executive Report");
        spark.config().setReportName("SpaceX CI/CD Consolidated Automation Run Results");

        String customCss = 
            ".nav-logo { " +
            "   width: 180px !important; " + 
            "   padding-left: 10px !important; " +
            "} " +
            ".nav-logo .logo { " +
            "   background-image: url('download.jpeg') !important; " + 
            "   background-size: contain !important; " +
            "   background-repeat: no-repeat !important; " +
            "   background-position: left center !important; " +
            "   width: 160px !important; " + 
            "   height: 40px !important; " +
            "   margin-top: 15px !important; " +
            "} " +
            ".header.navbar .vheader .nav-left { " +
            "   padding-left: 30px !important; " +
            "}";
        spark.config().setCss(customCss);

        extent.attachReporter(spark);
        parseDirectory(extent, Paths.get(inputArtifactsDir));
        parseDirectory(extent, Paths.get(fallbackOutputDir));
        extent.flush();
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
        
        String testcaseRegex = "<testcase\\s+name=\"([^\"]+)\"\\s+classname=\"([^\"]+)\"\\s+time=\"([^\"]+)\"\\s*(?:/>|>(.*?)</testcase>)";
        Matcher matcher = Pattern.compile(testcaseRegex, Pattern.DOTALL).matcher(content);
        
        while (matcher.find()) {
            String testName = matcher.group(1);
            String className = matcher.group(2);
            String duration = matcher.group(3);
            String failureBlock = matcher.group(4); 

            ExtentTest extentTest = extent.createTest("[" + suiteName + "] " + className + " -> " + testName);
            extentTest.assignCategory(suiteName);
            extentTest.info("Execution Duration: " + duration + " seconds");

            if (failureBlock != null && failureBlock.contains("<failure")) {
                String errorMessage = failureBlock.replaceAll("<failure[^>]*>", "").replaceAll("</failure>", "").trim();
                extentTest.log(Status.FAIL, "Test Execution Failed!");
                extentTest.fail("<pre>" + errorMessage + "</pre>");

                String screenshotFilename = testName + ".png";
                File screenshotFile = new File("build/reports/screenshots/" + screenshotFilename);

                if (screenshotFile.exists()) {
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