package pages;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * API "page" for the Users resource.
 * Mirrors LoginPage / AuthPage — each method is one named action
 * (e.g. loginPage.enterUsername()) mapped to one HTTP call.
 *
 * Usage:
 *   UsersApiPage users = new UsersApiPage(requestSpec);
 *   Response r = users.getUser(2);
 */
public class UsersApiPage {

    private final RequestSpecification spec;

    public UsersApiPage(RequestSpecification spec) {
        this.spec = spec;
    }

    // ── GET ──────────────────────────────────────────────────────────────────

    public Response getAllUsers(int page) {
        return given(spec)
                .queryParam("page", page)
                .when()
                .get("/api/users");
    }

    public Response getUser(int userId) {
        return given(spec)
                .when()
                .get("/api/users/{id}", userId);
    }

    // ── POST ─────────────────────────────────────────────────────────────────

    public Response createUser(String name, String job) {
        String body = String.format("{\"name\":\"%s\",\"job\":\"%s\"}", name, job);
        return given(spec)
                .body(body)
                .when()
                .post("/api/users");
    }

    // ── PUT ──────────────────────────────────────────────────────────────────

    public Response updateUser(int userId, String name, String job) {
        String body = String.format("{\"name\":\"%s\",\"job\":\"%s\"}", name, job);
        return given(spec)
                .body(body)
                .when()
                .put("/api/users/{id}", userId);
    }

    // ── PATCH ─────────────────────────────────────────────────────────────────

    public Response patchUser(int userId, String job) {
        String body = String.format("{\"job\":\"%s\"}", job);
        return given(spec)
                .body(body)
                .when()
                .patch("/api/users/{id}", userId);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public Response deleteUser(int userId) {
        return given(spec)
                .when()
                .delete("/api/users/{id}", userId);
    }
}