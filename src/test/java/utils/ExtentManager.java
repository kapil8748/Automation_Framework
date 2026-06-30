package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;
    // ThreadLocal ensures thread-safety if running tests in parallel
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("build/reports/ExtentReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Build Tool", "Gradle");
        }
        return extent;
    }

    public static void createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        testThread.set(test);
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}