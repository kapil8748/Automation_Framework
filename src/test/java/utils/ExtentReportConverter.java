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

        File reportDir = new File("build/reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        try {
            Path sourceLogo = Paths.get("src/test/resources/download.jpeg");
            if (!Files.exists(sourceLogo)) {
                sourceLogo = Paths.get("download.jpeg");
            }
            Path targetLogo = Paths.get("build/reports/download.jpeg");
            if (Files.exists(sourceLogo)) {
                Files.copy(sourceLogo, targetLogo, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Branding Asset Loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("Failed to transfer logo asset: " + e.getMessage());
        }

        ExtentReports extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(reportTargetDestination);
        
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("ABC Company Automation Framework - Unified Executive Report");
        spark.config().setReportName("ABC Company CI/CD Consolidated Automation Run Results");

        String customCss = 
            ".nav-logo { width: 180px !important; padding-left: 10px !important; } " +
            ".nav-logo .logo { background-image: url('download.jpeg') !important; background-size: contain !important; background-repeat: no-repeat !important; background-position: left center !important; width: 160px !important; height: 40px !important; margin-top: 15px !important; } " +
            ".header.navbar .vheader .nav-left { padding-left: 30px !important; }";
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
        
        // Fixed regex: Captures the full outer node cleanly without bleed-through to adjacent sibling tags
        String testcaseBlockRegex = "<testcase\\s+[^>]*>(?:.*?</testcase>|)";
        Matcher blockMatcher = Pattern.compile(testcaseBlockRegex, Pattern.DOTALL).matcher(content);
        
        // Individual field extraction expressions
        Pattern namePattern = Pattern.compile("name=\"([^\"]+)\"");
        Pattern classPattern = Pattern.compile("classname=\"([^\"]+)\"");
        Pattern timePattern = Pattern.compile("time=\"([^\"]+)\"");

        while (blockMatcher.find()) {
            String testcaseBlock = blockMatcher.group();

            Matcher mName = namePattern.matcher(testcaseBlock);
            Matcher mClass = classPattern.matcher(testcaseBlock);
            Matcher mTime = timePattern.matcher(testcaseBlock);

            if (!mName.find() || !mClass.find() || !mTime.find()) {
                continue; // Skip malformed nodes smoothly
            }

            String testName = mName.group(1);
            String className = mClass.group(1);
            String duration = mTime.group(1);

            ExtentTest extentTest = extent.createTest("[" + suiteName + "] " + className + " -> " + testName);
            extentTest.assignCategory(suiteName);
            extentTest.info("Execution Duration: " + duration + " seconds");

            if (testcaseBlock.contains("<failure")) {
                // Safely extract content between <failure> tags
                String errorMessage = testcaseBlock.replaceAll("(?s).*<failure[^>]*>", "")
                                                   .replaceAll("(?s)</failure>.*", "")
                                                   .trim();
                
                extentTest.log(Status.FAIL, "Test Execution Failed!");
                extentTest.fail("<pre>" + errorMessage + "</pre>");

                // --- FUZZY SCREENSHOT MATCHING ENGINE ---
                File masterScreenshotDir = new File("build/reports/screenshots");
                if (!masterScreenshotDir.exists()) {
                    masterScreenshotDir.mkdirs();
                }

                String cleanClassName = className.contains(".") ? className.substring(className.lastIndexOf(".") + 1) : className;
                File resolvedScreenshot = findScreenshotMatch(testName, cleanClassName);

                if (resolvedScreenshot != null && resolvedScreenshot.exists()) {
                    String destinationFilename = resolvedScreenshot.getName();
                    File localDestination = new File(masterScreenshotDir, destinationFilename);
                    
                    if (!resolvedScreenshot.getAbsolutePath().equals(localDestination.getAbsolutePath())) {
                        Files.copy(resolvedScreenshot.toPath(), localDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }

                    String relativeReportPath = "screenshots/" + destinationFilename;
                    extentTest.fail("Failure Screen Capture:", 
                        MediaEntityBuilder.createScreenCaptureFromPath(relativeReportPath).build());
                    System.out.println("Attached screenshot match: " + destinationFilename);
                } else {
                    extentTest.info("No screenshot match found for test: " + testName + " or class: " + cleanClassName);
                }

            } else if (testcaseBlock.contains("<skipped")) {
                extentTest.log(Status.SKIP, "Test Case Skipped.");
            } else {
                extentTest.log(Status.PASS, "Passed successfully.");
            }
        }
    }

    private static File findScreenshotMatch(String testName, String className) {
        String[] lookupFolders = {
            "build/reports/screenshots",
            "build/artifacts/test1/screenshots",
            "build/artifacts/test2/screenshots",
            "build/artifacts/test3/screenshots",
            "build/screenshots"
        };

        for (String folderPath : lookupFolders) {
            File dir = new File(folderPath);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                            if (name.contains(testName.toLowerCase()) || name.contains(className.toLowerCase())) {
                                return f;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}