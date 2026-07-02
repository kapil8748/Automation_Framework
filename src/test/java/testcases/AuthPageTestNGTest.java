package testcases;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import pages.AuthPage;

public class AuthPageTestNGTest {
    private AuthPage page;

    @BeforeMethod
    public void init() {
        page = new AuthPage();
    }

    // You can assign multiple groups to a single test method easily
    @Test(groups = {"Auth_Page_TestNG_Test", "specificTestNGTest"})
    public void testValidLogin() {
        assertTrue(page.login("Admin", "Secret123"), "Valid login failed!");
    }

    @Test(groups = "Auth_Page_TestNG_Test")
    public void testInvalidLogin() {
        assertFalse(page.login("user", "wrongpass"), "Invalid login allowed access!");
    }

    @Test(groups = "Auth_Page_TestNG_Test", expectedExceptions = IllegalArgumentException.class)
    public void testNullCredentialsThrowsException() {
        page.login(null, "pass");
    }
}