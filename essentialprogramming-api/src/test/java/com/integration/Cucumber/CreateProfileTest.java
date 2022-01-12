package com.integration.Cucumber;

import com.api.model.ProfileInput;
import com.api.model.Role;
import com.api.model.UserInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.util.TestEntityGenerator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;

public class CreateProfileTest {

    private Response response;
    private String adminAccessToken;
    private String userAccessToken;

    ObjectMapper objectMapper = new ObjectMapper();
    static UserInput userInput ;
    ProfileInput profileInput = TestEntityGenerator.generateProfileInput(Role.CEO);

    @Given("Admin is logged in and create user endpoint exists")
    public void adminLoginAndSetEndpoint() {

        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/security/employee/create";
        //TODO: admin login
    }

    @When("Admin sends a valid create user payload")
    public void adminCreatesUser() {
        userInput = TestEntityGenerator.generateUserInput();
        response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
//                .header("Authorization", adminAccessToken)
                .header("Accept-Language", "en")
                .body(userInput)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    @Then("Response status code should be 201")
    public void checkResponseStatusCode() {

        Assertions.assertEquals(201, response.getStatusCode());
    }

    @And("Create user response should be valid")
    public void verifyResponse() {

        Assertions.assertEquals(response.jsonPath().get("firstName"), userInput.getFirstName());
        Assertions.assertEquals(response.jsonPath().get("lastName"), userInput.getLastName());
        Assertions.assertEquals(response.jsonPath().get("email"), userInput.getEmail());
        Assertions.assertEquals(response.jsonPath().get("phone"), userInput.getPhone());
    }


    @Given("User login endpoint exists")
    public void setLoginEndpoint() {

        RestAssured.basePath = "/api/auth/token";
    }

    @When("The user sends valid credentials")
    public void userLogin() {
        ObjectNode userDetails = objectMapper.createObjectNode();
        userDetails.put("email", userInput.getEmail());
        userDetails.put("password", "P@rola123");

        response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Accept-Language", "en")
                .body(userDetails)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }


    @Then("Response status should be 200")
    public void checkResponseStatusCodeForUserLogin() {

        Assertions.assertEquals(200, response.getStatusCode());
    }

    @And("Extracted token should not be null")
    public void extractUserAccessToken() {

        Assertions.assertNotNull(response.jsonPath().get("accessToken"));
        userAccessToken = "Bearer " + response.jsonPath().get("accessToken");
    }

    @When("The user sends invalid credentials")
    public void userSendsInvalidCredentials() {
        ObjectNode userDetails = objectMapper.createObjectNode();
        userDetails.put("email", userInput.getEmail());
        userDetails.put("password", "Wrong password");

        response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Accept-Language", "en")
                .body(userDetails)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    @Then("Response status should be 500")
    public void checkResponseStatusCodeForInvalidCredentials() {
        Assertions.assertEquals(500, response.getStatusCode());
    }

    @And("Extracted token should be null and the response message \"The password you entered is wrong. Please Try again.\"")
    public void checkResponse() {
        Assertions.assertNull(response.jsonPath().get("accessToken"));
        Assertions.assertEquals("The password you entered is wrong. Please Try again.",
                response.jsonPath().get("message"));
    }


    @Given("User is logged in")
    public void userLoginn() {
        RestAssured.basePath = "/api/auth/token";
        ObjectNode userDetails = objectMapper.createObjectNode();
        userDetails.put("email", userInput.getEmail());
        userDetails.put("password", "P@rola123");

        userAccessToken = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Accept-Language", "en")
                .body(userDetails)
                .when()
                .post()
                .then()
                .extract()
                .path("accessToken");
        userAccessToken = "Bearer " + userAccessToken;
    }

    @When("User sends profile data")
    public void createProfile() {
        RestAssured.basePath = "/api/profile";
        response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", userAccessToken)
                .header("Accept-Language", "en")
                .body(profileInput)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    @And("Create profile response should be valid")
    public void verifyCreateProfileResponse() {

        //TODO Make test upload a file
       // Assertions.assertEquals(response.jsonPath().get("profilePicture"), profileInput.getProfilePicture());
        Assertions.assertEquals(response.jsonPath().get("firstName"), profileInput.getFirstname());
        Assertions.assertEquals(response.jsonPath().get("lastName"), profileInput.getLastname());
        Assertions.assertEquals(response.jsonPath().get("email"), userInput.getEmail());
        Assertions.assertEquals(response.jsonPath().get("phone"), profileInput.getPhone());
        Assertions.assertEquals(response.jsonPath().get("summary"), profileInput.getSummary());
        Assertions.assertEquals(response.jsonPath().get("role"), profileInput.getRole());
        Assertions.assertEquals(response.jsonPath().get("projects"), profileInput.getProjectList());
        Assertions.assertEquals(response.jsonPath().get("spokenLanguages"), profileInput.getSpokenLanguages());
        Assertions.assertEquals(response.jsonPath().get("skills"), profileInput.getSkillList());

        RestAssured.reset();
    }

}
