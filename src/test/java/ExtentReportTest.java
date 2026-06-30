import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// We register the custom retry interceptor directly onto your test class
@ExtendWith(ExtentReportTest.RetryInterceptor.class)
public class ExtentReportTest {

    private static ExtentReports extent;
    // Made static so the Interceptor extension can easily log status across retries
    private static ExtentTest test; 

    @BeforeAll
    public static void setupReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("build/reports/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester", "Your Name");
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
    public void sampleFlakeyTest() {
        test.info("Starting sampleFlakeyTest");
        // This will fail on purpose to demonstrate the retry mechanism
        fail("Simulated test failure to trigger retry logic.");
    }

    // --- Embedded Retry Interceptor ---
    public static class RetryInterceptor implements InvocationInterceptor {
        private static final int MAX_RETRIES = 2; // Number of times to retry a failed test

        @Override
        public void interceptTestMethod(Invocation<Void> invocation,
                                        ReflectiveInvocationContext<Method> invocationContext,
                                        ExtensionContext extensionContext) throws Throwable {
            
            // 1. Automatically initialize the Extent Test before execution (Replaces @BeforeEach)
            String testName = extensionContext.getDisplayName();
            test = extent.createTest(testName);

            int attempt = 0;
            Throwable lastThrowable = null;

            while (attempt <= MAX_RETRIES) {
                try {
                    if (attempt > 0) {
                        test.info("Retrying test execution: Attempt " + attempt);
                    }
                    
                    invocation.proceed(); // Execute the actual test method
                    
                    // 2. If it reaches here, the test passed successfully!
                    test.pass("Test passed successfully.");
                    return; 
                } catch (Throwable t) {
                    lastThrowable = t;
                    attempt++;
                    
                    if (attempt <= MAX_RETRIES) {
                        test.warning("Attempt " + attempt + " failed: " + t.getMessage());
                    }
                }
            }
            
            // 3. If all retries are exhausted and it still fails, log it to Extent and fail the build (Replaces @AfterEach)
            test.fail("Test completely failed after " + MAX_RETRIES + " retries. Reason: " + lastThrowable.getMessage());
            throw lastThrowable;
        }
    }
}