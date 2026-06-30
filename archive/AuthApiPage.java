package pages;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * API "page" for Authentication endpoints.
 * Mirrors AuthPage.java on the Selenium side.
 *
 * Usage:
 *   AuthApiPage auth = new AuthApiPage(requestSpec);
 *   Response r = auth.login("eve.holt@reqres.in", "cityslicka");
 *   String token = r.jsonPath().getString("token");
 *   RestApiBase.setAuthToken(token);
 */
public class AuthApiPage {

    private final RequestSpecification spec;

    public AuthApiPage(RequestSpecification spec) {
        this.spec = spec;
    }

    /**
     * POST /api/login
     * Returns 200 + token on success, 400 on bad credentials.
     */
    public Response login(String email, String password) {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        return given(spec)
                .body(body)
                .when()
                .post("/api/login");
    }

    /**
     * POST /api/register
     * Returns 200 + id + token on success, 400 on missing fields.
     */
    public Response register(String email, String password) {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        return given(spec)
                .body(body)
                .when()
                .post("/api/register");
    }

    /**
     * POST /api/login with only an email (no password) — for negative testing.
     */
    public Response loginMissingPassword(String email) {
        String body = String.format("{\"email\":\"%s\"}", email);
        return given(spec)
                .body(body)
                .when()
                .post("/api/login");
    }
}