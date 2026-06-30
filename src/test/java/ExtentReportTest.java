import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ExtentReportTest {

    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    public static void setupReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("build/reports/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester", "Your Name");
    }

    @BeforeEach
    public void startTest(TestInfo testInfo) {
        // JUnit 5 injects TestInfo automatically to get the test name
        String testName = testInfo.getTestMethod().isPresent() 
                ? testInfo.getTestMethod().get().getName() 
                : testInfo.getDisplayName();
                
        test = extent.createTest(testName);
    }

    @AfterEach
    public void tearDownTest() {
        if (test.getStatus() != Status.FAIL) {
            test.pass("Test passed successfully");
        }
    }

    @AfterAll
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    @Test
    public void samplePassTest() {
        test.info("Starting samplePassTest");
        assertTrue(true, "This should pass");
    }

    @Test
    public void sampleFailTest() {
        test.info("Starting sampleFailTest");
        try {
            fail("Intentional failure for demonstration");
        } catch (AssertionError e) {
            test.fail(e.getMessage());
            throw e; 
        }
    }
}