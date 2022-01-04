package com.api.controller;

import com.api.config.Anonymous;
import com.api.model.ProfileSkillSearchCriteria;
import com.api.model.SkillSearchCriteria;
import com.api.output.CategorySkillRelationJSON;
import com.api.output.ProfileSkillJSON;
import com.api.output.SkillJSON;
import com.api.service.SkillService;
import com.exception.ExceptionHandler;
import com.token.validation.auth.AuthUtils;
import com.util.async.Computation;
import com.util.async.ExecutorsProvider;
import com.util.enums.Language;
import com.util.web.JsonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
@Tag(description = "Skill API", name = "Skill Services")
@Path("/skill")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SkillController {

    private final SkillService skillService;

    @Context
    private Language language;

    @POST
    @Path("/add-category")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add new skill category",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Adds a new skill category," +
                            " if valid and does not already exist" +
                            " and returns a custom JSON response if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"created\", " +
                                            "\"message\": \"Skill suggestion added!\", " +
                                            "\"categoryKey\": \"key\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @RolesAllowed({"ADMIN"})
    @Anonymous
    public void addNewSkillCategory(@Valid @NotNull(message = "Skill category name must be provided!")
                                    @QueryParam("name") String name,
                                    @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> addSkillCategory(name), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(201).entity(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private JsonResponse addSkillCategory(String name) {
        return skillService.addSkillCategory(name);
    }


    @POST
    @Path("/suggest")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Suggest a new skill",
            responses = {
                    @ApiResponse(responseCode = "201", description = "A user can suggest a non-existing skill" +
                            " and a custom JSON response is returned if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"created\", " +
                                            "\"message\": \"Skill suggestion added!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    public void suggestSkill(@Valid @NotNull(message = "Skill name must be provided!")
                             @QueryParam("skillName") String skillName,
                             @Valid @NotNull(message = "Skill category name must be provided!")
                             @QueryParam("categoryKey") String categoryKey,
                             @HeaderParam("Authorization") String authorization,
                             @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> suggestSkill(email, skillName, categoryKey), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(201).entity(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private JsonResponse suggestSkill(String email, String skillName, String categoryKey) {
        return skillService.suggestSkill(email, skillName, categoryKey);
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add new skill",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Adds a new skill for an existing category" +
                            " if valid and does not already exist" +
                            " and returns the skill if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SkillJSON.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @RolesAllowed({"ADMIN"})
    @Anonymous
    public void addNewSkill(@Valid @NotNull(message = "Skill name must be provided!")
                            @QueryParam("skillName") String skillName,
                            @Valid @NotNull(message = "Category key must be provided!")
                            @QueryParam("skillCategoryKey") String skillCategoryKey,
                            @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> addNewSkill(skillName, skillCategoryKey), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(201).entity(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable addNewSkill(String skillName, String skillCategoryKey) {
        return skillService.addNewSkill(skillName, skillCategoryKey);
    }


    @POST
    @Path("/accept")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "The admin can accept a skill suggestion",
            responses = {
                    @ApiResponse(responseCode = "201", description = "The admin can accept a skill suggestion," +
                            " an email is sent to the user that made the suggestion" +
                            " and a custom JSON response is returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"created\", " +
                                            "\"message\": \"New skill added!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")})
    @RolesAllowed("ADMIN")
    @Anonymous
    public void acceptSuggestSkill(@Valid @NotNull(message = "Suggested skill key must be provided!")
                                   @QueryParam("suggestedSkillKey") String suggestedSkillKey,
                                   @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> acceptSuggestSkill(suggestedSkillKey), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(201).entity(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private JsonResponse acceptSuggestSkill(String suggestedSkillKey) {
        return skillService.acceptSuggestSkill(suggestedSkillKey, language);
    }


    @DELETE
    @Path("/deny")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "The admin can deny a skill suggestion",
            responses = {
                    @ApiResponse(responseCode = "204", description = "The admin can deny a skill suggestion," +
                            " an email is sent to the user that made the suggestion" +
                            " and a custom JSON response is returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"no content\", " +
                                            "\"message\": \"Skill suggestion was denied!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")})
    @RolesAllowed("ADMIN")
    @Anonymous
    public void denySuggestSkill(@Valid @NotNull(message = "Suggested skill key must be provided!")
                                 @QueryParam("suggestedSkillKey") String suggestedSkillKey,
                                 @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> denySuggestSkill(suggestedSkillKey), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(204).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private JsonResponse denySuggestSkill(String suggestedSkillKey) {
        return skillService.denySuggestSkill(suggestedSkillKey, language);
    }


    @PUT
    @Path("/edit/skill/name")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Edit the name of a skill",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Edit the name of an already existing skill" +
                            " and returns a custom JSON response if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"no content\", " +
                                            "\"message\": \"Skill successfully edited!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @RolesAllowed({"ADMIN"})
    @Anonymous
    public void editSkill(@Valid @NotNull(message = "Skill name that need to be edited must be provided!")
                          @QueryParam("Skill name") String skillName,
                          @Valid @NotNull(message = "The new skill name must be provided!")
                          @QueryParam("New skill name") String newSkillName,
                          @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> editSkillName(skillName, newSkillName), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(204).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private JsonResponse editSkillName(String skillName, String newSkillName) {
        return skillService.editSkillName(skillName, newSkillName);
    }



    @PUT
    @Path("/edit/skill/category")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Edit the category of a skill",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Edit the category of an already existing skill " +
                            "with a category that already exist" +
                            " and returns a custom JSON response if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"no content\", " +
                                            "\"message\": \"Skill successfully edited!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")})
    @RolesAllowed({"ADMIN"})
    @Anonymous
    public void editCategoryOfASkill(@Valid @NotNull(message = "Skill name that need to be edited must be provided!")
                                     @QueryParam("Skill name") String skillName,
                                     @Valid @NotNull(message = "The category key must be provided!")
                                     @QueryParam("The new category key") String newCategoryKey,
                                     @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> editCategoryOfASkill(skillName, newCategoryKey), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(204).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private JsonResponse editCategoryOfASkill(String skillName, String newCategoryKey) {
        return skillService.editCategoryOfASkill(skillName, newCategoryKey);
    }

    @POST
    @Path("search/skills")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @Operation(summary = "Search skills",
            responses  = {
                    @ApiResponse(responseCode = "200", description = "Get a list of skills based on given search criteria",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategorySkillRelationJSON.class))),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "422", description = "Name search criteria must have at least 2 characters!"),
                    @ApiResponse(responseCode = "422", description = "Category search criteria must have at least 2 characters!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void searchSkills(@Valid SkillSearchCriteria skillSearchCriteria, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> searchSkills(skillSearchCriteria), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    public Serializable searchSkills(SkillSearchCriteria skillSearchCriteria) {
        return (Serializable) skillService.searchSkills(skillSearchCriteria);
    }

    @POST
    @Path("search/profile/skills")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @Operation(summary = "Search skills associated to a profile",
            responses  = {
                    @ApiResponse(responseCode = "200", description = "Get a list of skills associated to a profile based on given search criteria",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileSkillJSON.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "422", description = "Business error."),
                    @ApiResponse(responseCode = "422", description = "Name search criteria must have at least 2 characters!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void searchProfileSkills(@HeaderParam("Authorization") String authorization, @Valid ProfileSkillSearchCriteria profileSkillSearchCriteria, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer,"email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> searchProfileSkills(profileSkillSearchCriteria, userEmail), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    public Serializable searchProfileSkills(ProfileSkillSearchCriteria profileSkillSearchCriteria, String userEmail) {
        return (Serializable) skillService.searchProfileSkills(profileSkillSearchCriteria, userEmail);
    }
}
