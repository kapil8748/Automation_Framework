package testcases;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;
import pages.AuthPage;

public class AuthPageTestNGTest {
    private AuthPage page;

    @BeforeMethod
    public void init() {
        page = new AuthPage();
    }

    @Test
    public void testValidLogin() {
        // TestNG signature structure: assertTrue(actual, message)
        assertTrue(page.login("Admin", "Secret123"), "Valid login failed!");
    }

    @Test
    public void testInvalidLogin() {
        assertFalse(page.login("user", "wrongpass"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullCredentialsThrowsException() {
        // TestNG handles expected failures directly via annotations
        page.login(null, "pass");
    }
}
