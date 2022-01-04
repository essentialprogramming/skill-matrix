package com.integration.Cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SkillTest {

    public static String CATEGORY_NAME = "Frameworks";
    public static String SKILL_NAME = "Hibernate";

    private static String userAccessToken;
    private static String categoryKey;
    private static String skillKey;

    private String path;
    private Response response;

    ObjectMapper objectMapper = new ObjectMapper();

    @Given("the add category endpoint and admin token exists")
    public void addCategoryEndpointAndAdminTokenExists() {
        RestAssured.baseURI = "http://localhost:8080";
        path = "/api/skill/add-category";

        //get admin JWT
    }

    @When("an admin will submit the category name as a query parameter")
    public void addNewCategory() {
        response = given()
                    .header("Accept-Language", "en")
                    .queryParam("name", CATEGORY_NAME)
                .when()
                    .post(path)
                .then()
                    .extract()
                    .response();
    }

    @Then("the server will add that category to the database and return a JSON of it")
    public void verifyAddCategoryResponse() {
        String status = response.jsonPath().get("status");
        String message = response.jsonPath().get("message");
        categoryKey = response.jsonPath().get("categoryKey");

        assertEquals(201, response.getStatusCode());
        assertEquals("created", status);
        assertEquals(CATEGORY_NAME + " skill category successfully added.", message);
        assertNotNull(categoryKey);
    }

    @Then("the server will return 422 and category already exists")
    public void verifyUnsuccessfullyAddCategoryResponse() {
        String message = response.jsonPath().get("message");

        assertEquals(422, response.getStatusCode());
        assertEquals("422 This skill category already exists", message);
    }

    @Given("the add skill endpoint and admin token exists")
    public void addSkillEndpointAndAdminTokenExists() {
        RestAssured.baseURI = "http://localhost:8080";
        path = "/api/skill/add";

        //get admin JWT
    }

    @When("an admin will submit a skill name and category key as query parameters")
    public void addNewSkill() {
        response = given()
                    .header("Accept-Language", "en")
                    .queryParam("skillName", SKILL_NAME)
                    .queryParam("skillCategoryKey", categoryKey)
                .when()
                    .post(path)
                .then()
                    .extract()
                    .response();
    }

    @Then("the server will create a new skill for each skill level for the given category")
    public void verifyAddSkillResponse() {
        String skillName = response.jsonPath().get("name");
        List<String> skillKeys = response.jsonPath().get("skillKeys");

        skillKey = skillKeys.get(0);

        assertEquals(201, response.getStatusCode());
        assertEquals(SKILL_NAME, skillName);
        skillKeys.forEach(Assertions::assertNotNull);
    }

    @Then("the server will return 422 and skill already exists")
    public void verifyUnsuccessfullyAddSkillResponseSkillAlreadyExists() {
        String message = response.jsonPath().get("message");

        assertEquals(422, response.getStatusCode());
        assertEquals("422 This skill already exist", message);
    }

    @When("an admin will submit a skill name and category key as query parameters but the category does not exist")
    public void addNewSkillUnsuccessfully() {
        response = given()
                    .header("Accept-Language", "en")
                    .queryParam("skillName", SKILL_NAME)
                    .queryParam("skillCategoryKey", "IAMAKEYFORACATEGORYTHATDOESNOTEXIST")
                .when()
                    .post(path)
                .then()
                    .extract()
                    .response();
    }

    @Then("the server will return 404 and category not found")
    public void verifyUnsuccessfullyAddSkillResponseCategoryNotFound() {
        String message = response.jsonPath().get("message");

        assertEquals(404, response.getStatusCode());
        assertEquals("404 Skill category not found!", message);
    }

    @Given("associate a skill to a profile endpoint exists")
    public void addSkilToProfileEndpointExists() {
        RestAssured.baseURI = "http://localhost:8080";
        path = "/api/profile/add/profile/skill";

        ObjectNode user = objectMapper.createObjectNode();
        user.put("email", "adriana.zdrob@avangarde-software.com");
        user.put("password", "P@rola123");

        userAccessToken = given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .header("Accept-Language", "en")
                    .body(user)
                .when()
                    .post("/api/auth/token")
                .then()
                    .extract()
                    .path("accessToken");

        userAccessToken = "Bearer " + userAccessToken;
    }

    @When("a user will submit a skill key as a query parameter")
    public void addSkillToProfile() {
        response = given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .header("Accept-Language", "en")
                    .header("Authorization", userAccessToken)
                    .queryParam("Skill Key", skillKey)
                .when()
                    .post(path)
                .then()
                    .extract()
                    .response();
    }

    @Then("the server will associate that skill to the user's profile, based on the email extracted from the JWT")
    public void verifyAddSkillToProfileResponse() {
        String status = response.jsonPath().get("status");
        String message = response.jsonPath().get("message");

        assertEquals(200, response.getStatusCode());
        assertEquals("ok", status);
        assertEquals("Skill successfully added to the user with the email adriana.zdrob@avangarde-software.com", message);
    }

    @Given("associate a skill to a profile endpoint exists but no profile is created")
    public void addSkillToProfileEndpointExistsNoProfileCrated() {
        RestAssured.baseURI = "http://localhost:8080";
        path = "/api/profile/add/profile/skill";

        ObjectNode user = objectMapper.createObjectNode();
        user.put("email", "markos.kosa@avangarde-software.com");
        user.put("password", "P@rola123");

        userAccessToken = given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .header("Accept-Language", "en")
                    .body(user)
                .when()
                    .post("/api/auth/token")
                .then()
                    .extract()
                    .path("accessToken");

        userAccessToken = "Bearer " + userAccessToken;
    }

    @Then("the server will return 404 and profile not found")
    public void verifyAddSkillToProfileResponseUnsuccessfullyProfileNotFound() {
        String message = response.jsonPath().get("message");

        assertEquals(404, response.getStatusCode());
        assertEquals("404 Profile not found for the given email!", message);
    }

    @When("a user will submit a wrong skill key as a query parameter")
    public void addSkillToProfileWrongSkillKey() {
        response = given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .header("Accept-Language", "en")
                    .header("Authorization", userAccessToken)
                    .queryParam("Skill Key", "IAMANINCORRECTSKILLKEY")
                .when()
                    .post(path)
                .then()
                    .extract()
                    .response();
    }

    @Then("the server will return 404 and skill not found")
    public void verifyAddSkillToProfileResponseUnsuccessfullySkillNotFound() {
        String message = response.jsonPath().get("message");

        assertEquals(404, response.getStatusCode());
        assertEquals("404 Skill not found!", message);
    }
}
