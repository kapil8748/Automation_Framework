package testcases;

import base.RestApiBaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pages.UsersApiPage;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CRUD test cases for /api/users.
 * Mirrors LoginPageTest / RahulShettyPracticePageTest structure:
 *   - Extends RestApiBaseTest (same as Selenium tests extend BaseTest)
 *   - Uses a "page" object (UsersApiPage) instead of calling given().when().then() inline
 *   - Validates response status, headers, and body fields
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class UsersApiTest extends RestApiBaseTest {

    private static final UsersApiPage usersApi = new UsersApiPage(requestSpec);

    // ── GET ──────────────────────────────────────────────────────────────────

    @Test
    void tc01_getAllUsers_shouldReturn200AndPageData() {
        Response response = usersApi.getAllUsers(1);

        assertEquals(200, response.statusCode(), "Status code should be 200");

        response.then()
                .body("page", equalTo(1))
                .body("data", not(empty()))
                .body("data[0].id", notNullValue())
                .body("data[0].email", containsString("@"));

        System.out.println("[PASS] GET /api/users?page=1 → " + response.statusCode());
    }

    @Test
    void tc02_getSingleUser_shouldReturn200WithCorrectId() {
        int userId = 2;
        Response response = usersApi.getUser(userId);

        assertEquals(200, response.statusCode());

        response.then()
                .body("data.id", equalTo(userId))
                .body("data.email", not(emptyOrNullString()))
                .body("data.first_name", not(emptyOrNullString()));

        System.out.println("[PASS] GET /api/users/2 → first_name: "
                + response.jsonPath().getString("data.first_name"));
    }

    @Test
    void tc03_getSingleUser_notFound_shouldReturn404() {
        Response response = usersApi.getUser(9999);

        assertEquals(404, response.statusCode(), "Non-existent user should return 404");
        System.out.println("[PASS] GET /api/users/9999 → 404 as expected");
    }

    // ── POST ─────────────────────────────────────────────────────────────────

    @Test
    void tc04_createUser_shouldReturn201WithIdAndTimestamp() {
        Response response = usersApi.createUser("Rahul", "QA Lead");

        assertEquals(201, response.statusCode(), "Create should return 201");

        response.then()
                .body("name", equalTo("Rahul"))
                .body("job", equalTo("QA Lead"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue());

        System.out.println("[PASS] POST /api/users → id: "
                + response.jsonPath().getString("id"));
    }

    // ── PUT ──────────────────────────────────────────────────────────────────

    @Test
    void tc05_updateUser_shouldReturn200WithUpdatedFields() {
        Response response = usersApi.updateUser(2, "Rahul", "Senior QA");

        assertEquals(200, response.statusCode(), "PUT should return 200");

        response.then()
                .body("name", equalTo("Rahul"))
                .body("job", equalTo("Senior QA"))
                .body("updatedAt", notNullValue());

        System.out.println("[PASS] PUT /api/users/2 → updatedAt: "
                + response.jsonPath().getString("updatedAt"));
    }

    // ── PATCH ─────────────────────────────────────────────────────────────────

    @Test
    void tc06_patchUser_shouldReturn200WithPatchedField() {
        Response response = usersApi.patchUser(2, "Principal QA");

        assertEquals(200, response.statusCode(), "PATCH should return 200");

        response.then()
                .body("job", equalTo("Principal QA"))
                .body("updatedAt", notNullValue());

        System.out.println("[PASS] PATCH /api/users/2 → job updated");
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void tc07_deleteUser_shouldReturn204NoContent() {
        Response response = usersApi.deleteUser(2);

        assertEquals(204, response.statusCode(), "DELETE should return 204");
        assertTrue(response.body().asString().isBlank(),
                "DELETE body should be empty");

        System.out.println("[PASS] DELETE /api/users/2 → 204 No Content");
    }

    // ── Response header validation ────────────────────────────────────────────

    @Test
    void tc08_responseHeaders_shouldContainJsonContentType() {
        Response response = usersApi.getUser(1);

        String contentType = response.header("Content-Type");
        assertNotNull(contentType, "Content-Type header must be present");
        assertTrue(contentType.contains("application/json"),
                "Content-Type should be application/json");

        System.out.println("[PASS] Content-Type header: " + contentType);
    }
}