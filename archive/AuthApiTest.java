package testcases;

import base.RestApiBaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pages.AuthApiPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Auth test cases — mirrors AuthPageJunitTest on the Selenium side.
 * Covers: successful login, register, missing-password negative case,
 * and token extraction for use by other tests.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class AuthApiTest extends RestApiBaseTest {

    private static final AuthApiPage authApi = new AuthApiPage(requestSpec);

    @Test
    void tc01_login_validCredentials_shouldReturn200AndToken() {
        Response response = authApi.login("eve.holt@reqres.in", "cityslicka");

        assertEquals(200, response.statusCode(), "Login should return 200");

        String token = response.jsonPath().getString("token");
        assertNotNull(token, "Token must not be null on valid login");
        assertFalse(token.isBlank(), "Token must not be empty");

        setAuthToken(token);

        System.out.println("[PASS] Login → token: " + token);
    }

    @Test
    void tc02_login_missingPassword_shouldReturn400WithError() {
        Response response = authApi.loginMissingPassword("eve.holt@reqres.in");

        assertEquals(400, response.statusCode(), "Missing password should return 400");

        String error = response.jsonPath().getString("error");
        assertNotNull(error, "Error message must be present");
        assertEquals("Missing password", error);

        System.out.println("[PASS] Login (no password) → error: " + error);
    }

    @Test
    void tc03_register_validPayload_shouldReturn200WithIdAndToken() {
        Response response = authApi.register("eve.holt@reqres.in", "pistol");

        assertEquals(200, response.statusCode(), "Register should return 200");

        assertNotNull(response.jsonPath().getString("id"),    "id must be present");
        assertNotNull(response.jsonPath().getString("token"), "token must be present");

        System.out.println("[PASS] Register → id: " + response.jsonPath().getString("id"));
    }

    @Test
    void tc04_register_missingPassword_shouldReturn400() {
        Response response = authApi.loginMissingPassword("sydney@fife");

        assertEquals(400, response.statusCode(),
                "Register without password should return 400");

        String error = response.jsonPath().getString("error");
        assertNotNull(error, "Error field must be present");

        System.out.println("[PASS] Register (no password) → error: " + error);
    }

    @Test
    void tc05_authToken_isStoredAndRetrievable() {
        if (getAuthToken() == null) {
            Response r = authApi.login("eve.holt@reqres.in", "cityslicka");
            setAuthToken(r.jsonPath().getString("token"));
        }
        assertNotNull(getAuthToken(), "Token should be available for downstream tests");
        System.out.println("[PASS] Stored token is accessible: " + getAuthToken());
    }
}