package testcases;

import base.RestApiBaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

/**
 * Example test suite for Restful Booker API
 * Covers one test for each HTTP request type.
 */
@Tag("Restful_Booker_Api_Test")
public class RestfulBookerApiTest extends RestApiBaseTest {

    private static final String BASE_PATH = "/booking";

    @Test
    void testGetBookings() {
        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(BASE_PATH);

        response.then().statusCode(200)
                .body("$", not(empty()));
    }

    @Test
    void testCreateBooking() {
        String requestBody = """
            {
              "firstname" : "John",
              "lastname" : "Doe",
              "totalprice" : 120,
              "depositpaid" : true,
              "bookingdates" : {
                  "checkin" : "2026-07-01",
                  "checkout" : "2026-07-10"
              },
              "additionalneeds" : "Breakfast"
            }
            """;

        Response response = given()
                .spec(getRequestSpec())
                .body(requestBody)
                .when()
                .post(BASE_PATH);

        response.then().statusCode(200)
                .body("booking.firstname", equalTo("John"))
                .body("booking.lastname", equalTo("Doe"));
    }

    @Disabled
    void testUpdateBookingPut() {
        String requestBody = """
            {
              "firstname" : "Jane",
              "lastname" : "Smith",
              "totalprice" : 150,
              "depositpaid" : false,
              "bookingdates" : {
                  "checkin" : "2026-07-05",
                  "checkout" : "2026-07-15"
              },
              "additionalneeds" : "Dinner"
            }
            """;

        Response response = given()
                .spec(getRequestSpec())
                .auth().preemptive().basic("admin", "password123")
                .body(requestBody)
                .when()
                .put(BASE_PATH + "/1"); // updating booking id=1 for demo

        response.then().statusCode(anyOf(is(200), is(201)))
                .body("firstname", equalTo("Jane"));
    }

    @Disabled
    void testDeleteBooking() {
        Response response = given()
                .spec(getRequestSpec())
                .auth().preemptive().basic("admin", "password123")
                .when()
                .delete(BASE_PATH + "/1");

        response.then().statusCode(anyOf(is(200), is(201), is(204)));
    }
}
