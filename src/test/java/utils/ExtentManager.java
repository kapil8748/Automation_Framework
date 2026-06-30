package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testTracker = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("build/reports/extent-report.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }

    public static void createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        testTracker.set(test);
    }

    public static ExtentTest getTest() {
        return testTracker.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}