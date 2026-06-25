package testcases;

import pages.AuthPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;


public class AuthPageJunitTest {
    private AuthPage page;

    @Before
    public void init() {
        page = new AuthPage();
    }

    @Test
    public void testValidLogin() {
        assertTrue("Valid login failed!", page.login("Admin", "Secret123"));
    }
    
     @Test
    public void testInvalidLogin() {
        assertFalse(page.login("user", "wrongpass"));
    }

    @Test
    public void testNullCredentialsThrowsException() {
        // JUnit catches exceptions using lambda expressions
        assertThrows(IllegalArgumentException.class, () -> {
            page.login(null, "pass");
        });
    }
}

    