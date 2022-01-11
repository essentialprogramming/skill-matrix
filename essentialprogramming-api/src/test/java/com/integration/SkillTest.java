package com.integration;

import com.api.model.ProfileInput;
import com.api.model.ProjectInput;
import com.api.model.Role;
import com.api.model.UserInput;
import com.api.output.SkillJSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.util.TestEntityGenerator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SkillTest {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static String adminAccessToken;

    public static final int NUMBER_OF_CATEGORIES_TO_GENERATE = 3;
    public static final int NUMBER_OF_SKILLS_PER_CATEGORY = 3;
    public static final int NUMBER_OF_SKILLS_TO_ADD = 4;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8082";
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode userDetails = objectMapper.createObjectNode();
        userDetails.put("email", "florin.ciolea@avangarde.com");
        userDetails.put("password", "P@rola123");
//        userDetails.put("platform", "ADMIN");

        RestAssured.basePath = "api/auth/token";

        adminAccessToken = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Accept-Language", "en")
                .body(userDetails)
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("accessToken");

        adminAccessToken = "Bearer " + adminAccessToken;
        System.out.println(adminAccessToken);

    }


    //test flow: admin - create category skill and skills
    //                 - create user
    //   created user  - login
    //                 -create profile and add skills to profile
    //                 -create project and add skills to project
    @Test
    public void testFlow() {

        RestAssured.basePath = "/api";
        List<String> categoryKeys = new ArrayList<>();

        List<String> skillNames = new ArrayList<>();
        Map<String, String> skillNameAndKeys = new HashMap<>();

        //generate 3 categories, each with 3 skills. store in map skill name with associated key
        for (int i = 0; i < NUMBER_OF_CATEGORIES_TO_GENERATE; i++) {

            String categoryName = "categoryName" + secureRandom.nextInt();


            String categoryKey = given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .header("Authorization", adminAccessToken)
                    .header("Accept-Language", "en-US")
                    .queryParam("name", categoryName)
                    .when()
                    .post("/skill/category")
                    .then()
                    .assertThat()
                    .statusCode(201)
                    .and()
                    .contentType(ContentType.JSON)
                    .and()
                    .body("status", equalTo("created"))
                    .and()
                    .body("message", equalTo(categoryName + " skill category successfully added."))
                    .and()
                    .extract()
                    .path("categoryKey");

            for (int j = 0; j < NUMBER_OF_SKILLS_PER_CATEGORY; j++) {

                String skillName = "skillName" + secureRandom.nextInt();
                skillNames.add(skillName);

                String skillKey = given()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .header("Authorization", adminAccessToken)
                        .header("Accept-Language", "en-US")
                        .queryParam("skill-name", skillName)
                        .queryParam("skill-category-key", categoryKey)
                        .when()
                        .post("/skill")
                        .then()
                        .assertThat()
                        .statusCode(201)
                        .and()
                        .contentType(ContentType.JSON)
                        .and()
                        .extract()
                        .path("skillKey");

                skillNameAndKeys.put(skillName, skillKey);
            }

            categoryKeys.add(categoryKey);

        }
        Assertions.assertEquals(categoryKeys.size(), 3);
        Assertions.assertEquals(skillNameAndKeys.size(), 9);


        //create user
        UserInput userInput = TestEntityGenerator.generateUserInput();

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", adminAccessToken)
                .header("Accept-Language", "en")
                .body(userInput)
                .when()
                .post("/security/employee")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("email", equalTo(userInput.getEmail()))
                .and()
                .body("firstName", equalTo(userInput.getFirstName()))
                .and()
                .body("lastName", equalTo(userInput.getLastName()))
                .and()
                .body("phone", equalTo(userInput.getPhone()));


        //auth user
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode userDetails = objectMapper.createObjectNode();
        userDetails.put("email", userInput.getEmail());
        userDetails.put("password", "P@rola123");
//        userDetails.put("platform", "EMPLOYEE");

        String userToken = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Accept-Language", "en")
                .body(userDetails)
                .when()
                .post("/auth/token")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("accessToken");

        userToken = "Bearer " + userToken;
        System.out.println(userToken);


        //create profile for user
        RestAssured.basePath = "/api";
        ProfileInput profileInput = TestEntityGenerator.generateProfileInput(Role.CEO);

        String userEmail = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .header("Accept-Language", "en")
                .body(profileInput)
                .when()
                .post("/profile")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("profilePicture", equalTo(profileInput.getProfilePicture()))
                .and()
                .body("firstName", equalTo(profileInput.getFirstname()))
                .and()
                .body("lastName", equalTo(profileInput.getLastname()))
                .and()
                .body("email", equalTo(userInput.getEmail()))
                .and()
                .body("phone", equalTo(profileInput.getPhone()))
                .and()
                .body("summary", equalTo(profileInput.getSummary()))
                .and()
                .body("role", equalTo(profileInput.getRole()))
                .and()
                .body("projects", equalTo(profileInput.getProjectList()))
                .and()
                .body("spokenLanguages", equalTo(profileInput.getSpokenLanguages()))
                .and()
                .body("skills", equalTo(profileInput.getSkillList()))
                .extract()
                .path("email");


        //remove last element from map, so it will not be added to the profile; will use it in another test below
        String skillKeyRemoved = skillNameAndKeys.get(skillNames.get(skillNames.size() - 1));
        skillNameAndKeys.remove(skillNames.get(skillNames.size() - 1));
        //add skills to profile
        String finalUserToken = userToken;
        skillNameAndKeys.values().forEach(skillKey ->
                given()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .header("Authorization", finalUserToken)
                        .header("Accept-Language", "en")
                        //add skills
                        .queryParam("skill-key", skillKey)
                        .queryParam("skill-level", "BEGINNER")
                        .when()
                        .post("/profile/skill")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .and()
                        .body("status", equalTo("ok"))
                        .and()
                        .body("message", equalTo("Skill successfully added to the user with the email " + userEmail))

        );

        //verify if profile has the added skills
        List<SkillJSON> userProfileSkills = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .header("Accept-Language", "en")
                .when()
                .get("/profile")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("skills");

        Assertions.assertEquals(userProfileSkills.size(), 8);


        //create project for user
        ProjectInput projectInput = TestEntityGenerator.generateProjectInput();

        String projectKey = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .header("Accept-Language", "en")
                .body(projectInput)
                .when()
                .post("/profile/project")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("status", equalTo("ok"))
                .and()
                .body("message", equalTo("Project successfully added to the user with the email: " + userEmail))
                .extract()
                .path("projectKey");


        //add skills to project
        for (int i = 0; i < NUMBER_OF_SKILLS_TO_ADD; i++) {
            given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .header("Authorization", userToken)
                    .header("Accept-Language", "en")
                    .queryParam("project-key", projectKey)
                    .queryParam("skill-key", skillNameAndKeys.get(skillNames.get(i)))
                    .when()
                    .post("/profile/project/skill")
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .and()
                    .body("status", equalTo("ok"))
                    .and()
                    .body("message", equalTo("Skill successfully added to the project!"));
        }

        // test error on adding a skill to a project, a skill that does not exist in the profile
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .header("Accept-Language", "en")
                .queryParam("project-key", projectKey)
                .queryParam("skill-key", skillKeyRemoved)
                .when()
                .post("/profile/project/skill")
                .then()
                .assertThat()
                .statusCode(422)
                .and()
                .body("message", equalTo("The skill was not found in the skills list of the user's profile"))
                .and()
                .body("detail", equalTo("Ensure that the skill is in the skills list of the user's profile!"));


        //test status code generate profile pdf
//                given()
//                        .accept(ContentType.JSON)
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", userToken)
//                        .header("Accept-Language", "en")
//                        .when()
//                        .post("/profile/pdf")
//                        .then()
//                        .assertThat()
//                        .statusCode(200);

    }


    @AfterAll
    public static void afterAll() {
        RestAssured.reset();
    }
}
