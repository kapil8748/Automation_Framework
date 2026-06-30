package base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Base class for API setup.
 * Mirrors SeleniumBase but for REST Assured.
 */
public class RestApiBase {

    protected static RequestSpecification requestSpec;
    protected static String baseUrl = "https://restful-booker.herokuapp.com"; // default

    private static String authToken;

    /**
     * Initialize API client with base URL and JSON content type.
     */
    public static void setupApi() {
        System.out.println("[RestApiBase] Initializing API client for: " + baseUrl);

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.requestSpecification = requestSpec;
    }

    /**
     * Expose current RequestSpecification.
     */
    public static RequestSpecification getRequestSpec() {
        if (requestSpec == null) {
            throw new IllegalStateException("API not initialized. Call setupApi() first.");
        }
        return requestSpec;
    }

    // --- Token management helpers ---
    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }
}
