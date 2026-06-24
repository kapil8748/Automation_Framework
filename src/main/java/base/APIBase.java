package base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

/**
 * APIBase class to initialize and provide a reusable RequestSpecification
 */
public class APIBase {

    private static RequestSpecification requestSpec;

    /**
     * Initialize the RequestSpecification with base URI, headers, etc.
     * @param baseUri The root URI of the API
     */
    public static RequestSpecification initializeRequest(String baseUri) {
        System.out.println("Step 1: Initializing API RequestSpecification...");

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();

        RestAssured.requestSpecification = requestSpec;

        System.out.println("Step 2: RequestSpecification ready for API automation.");
        return requestSpec;
    }

    /**
     * Clear the RequestSpecification after tests
     */
    public static void closeRequest() {
        if (requestSpec != null) {
            requestSpec = null;
            RestAssured.requestSpecification = null;
            System.out.println("Step 3: RequestSpecification cleared successfully.");
        }
    }
}
