package testcases;

import pages.AuthPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Auth_Page_Junit_Test")
public class AuthPageJunitTest {
    private AuthPage page;

    @BeforeEach
    public void init() {
        page = new AuthPage();
    }

    // 1. Parameterized test for standard login combinations (Valid & Invalid)
    @Tag("specificJunitTest")
    @ParameterizedTest(name = "Login with username={0}, password={1} should return {2}")
    @CsvSource({
        "Admin, Secret123, true",  
        "user,  wrongpass, false", 
        "admin, Secret123, false", 
        "Admin, wrong,     false", 
        "'',    '',        false"   
    })
    public void LoginSituation(String username, String password, boolean expectedResult) {
        assertEquals(expectedResult, page.login(username, password));
    }

    
    @ParameterizedTest(name = "Null credentials should throw exception - username={0}, password={1}")
    @MethodSource("provideNullCredentials")
    public void testNullCredentialsThrowsException(String username, String password) {
        assertThrows(IllegalArgumentException.class, () -> {
            page.login(username, password);
        });
    }

    // Helper method to feed null combinations to the exception test
    private static Stream<Arguments> provideNullCredentials() {
        return Stream.of(
            Arguments.of(null, "pass"),
            Arguments.of("Admin", null),
            Arguments.of(null, null)
        );
    }
}