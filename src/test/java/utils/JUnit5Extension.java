package utils;

import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JUnit5Extension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) {
        ExtentManager.createTest("JUnit5: " + context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            ExtentManager.getTest().log(Status.FAIL, "Test Failed: " + context.getExecutionException().get());
        } else {
            ExtentManager.getTest().log(Status.PASS, "Test Passed");
        }
        ExtentManager.flush(); // Flush frequently since JUnit 5 doesn't provide a global suite-finish hook easily via extensions
    }
}