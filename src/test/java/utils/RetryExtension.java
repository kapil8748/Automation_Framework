package utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.lang.reflect.Method;

public class RetryExtension implements InvocationInterceptor, TestWatcher {

    private static final int MAX_RETRIES = 2; // Number of retries allowed

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        
        String testName = extensionContext.getDisplayName();
        ExtentManager.createTest(testName);
        
        int attempt = 0;
        Throwable lastThrowable = null;

        // We use a custom loop logic. To avoid calling the strict 'invocation.proceed()' multiple times,
        // we manually invoke the test method via reflection if it's a retry.
        while (attempt <= MAX_RETRIES) {
            try {
                if (attempt > 0) {
                    ExtentManager.getTest().info("Retrying test: Attempt " + attempt);
                    
                    // For retries, we invoke the method directly on the test instance to avoid JUnit's chain guard
                    Object testInstance = invocationContext.getTarget().orElseThrow(
                        () -> new IllegalStateException("Test instance not found")
                    );
                    invocationContext.getExecutable().invoke(testInstance);
                } else {
                    // First attempt goes through the normal chain cleanly
                    invocation.proceed();
                }
                return; // If it passes (either first try or retry), exit loop
            } catch (Throwable t) {
                // Unwrap the exception if it came from reflection via a retry
                if (t instanceof java.lang.reflect.InvocationTargetException) {
                    lastThrowable = t.getCause();
                } else {
                    lastThrowable = t;
                }
                
                attempt++;
                if (attempt <= MAX_RETRIES) {
                    ExtentManager.getTest().warning("Failed attempt " + attempt + ": " + lastThrowable.getMessage());
                }
            }
        }
        
        // If all retries fail, throw the final error to mark the test as failed
        throw lastThrowable;
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        ExtentManager.getTest().pass("Test passed successfully.");
        ExtentManager.flush();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ExtentManager.getTest().fail("Test failed after retries. Reason: " + cause.getMessage());
        ExtentManager.flush();
    }
}