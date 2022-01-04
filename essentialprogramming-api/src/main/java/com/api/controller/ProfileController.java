package com.api.controller;

import com.api.model.ProjectInput;
import com.api.model.SimpleProfileInput;
import com.api.output.ProfileJSON;
import com.api.output.ProjectJSON;
import com.api.output.SimpleProfileJSON;
import com.api.service.ProfileService;
import com.api.config.Anonymous;
import com.api.model.ProfileInput;
import com.exception.ExceptionHandler;
import com.template.model.Template;
import com.template.service.TemplateService;
import com.token.validation.auth.AuthUtils;
import com.util.async.Computation;
import com.util.async.ExecutorsProvider;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
@Tag(description = "Profile API", name = "Profile Services")
@Path("/profile")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProfileController {

    @Context
    private Language language;


    private final ProfileService profileService;
    private final TemplateService templateService;

    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create user profile",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Returns user profile if successfully added",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileJSON.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "User does not exist!"),
                    @ApiResponse(responseCode = "422", description = "Profile is already created!"),
                    @ApiResponse(responseCode = "422", description = "Role not found!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void createProfile(@HeaderParam("Authorization") String authorization, ProfileInput profileInput, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> createProfile(userEmail, profileInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.status(201).entity(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable createProfile(String userEmail, ProfileInput profileInput) throws ApiException {
        return profileService.createProfile(userEmail, profileInput);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the profile of an user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns a JSON of the user's profile.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileJSON.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found for the given user!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    public void getProfile(@HeaderParam("Authorization") String authorization, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> getProfile(userEmail), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    public Serializable getProfile(String userEmail) {
        return profileService.getProfile(userEmail);
    }


    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update user profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns user profile updated.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProfileJSON.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "Profile not found for the given user!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void updateProfile(@HeaderParam("Authorization") String authorization, SimpleProfileInput simpleProfileInput, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> updateProfile(userEmail, simpleProfileInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable updateProfile(String userEmail, SimpleProfileInput simpleProfileInput) throws ApiException {
        return profileService.updateProfile(userEmail, simpleProfileInput);
    }

    @POST
    @Path("/add/project")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add project to profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Add project to user's profile" +
                            " and returns a custom JSON response if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"ok\", " +
                                            "\"message\": \"Project successfully added!\", " +
                                            "\"projectKey\": \"key\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "User does not exist!"),
                    @ApiResponse(responseCode = "422", description = "Role not found!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void addProjectToProfile(@HeaderParam("Authorization") String authorization,
                                    ProjectInput projectInput,
                                    @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> addProjectToProfile(userEmail, projectInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable addProjectToProfile(String userEmail, ProjectInput projectInput) {
        return profileService.addProjectToProfile(userEmail, projectInput);
    }

    @POST
    @Path("/add/project/skill")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add skill to project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Add skill to a profile's project" +
                            " and returns a custom JSON response if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"ok\", " +
                                            "\"message\": \"Skill successfully added to the project!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "User does not exist!"),
                    @ApiResponse(responseCode = "422", description = "Role not found!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void addSkillToProject(@HeaderParam("Authorization") String authorization,
                                  @Valid @NotNull(message = "Project key must be provided!")
                                  @QueryParam("Project Key") String projectKey,
                                  @Valid @NotNull(message = "Skill key must be provided!")
                                  @QueryParam("Skill Key") String skillKey,
                                  @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> addSkillToProject(userEmail, projectKey, skillKey), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable addSkillToProject(String userEmail, String projectKey, String skillKey) {
        return profileService.addSkillToProject(projectKey, skillKey);
    }

    @POST
    @Path("/add/skill")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add skill to profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Add skill to user's profile" +
                            " and returns a custom JSON response if it was successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"status\": \"ok\", " +
                                            "\"message\": \"Skill successfully added!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "User does not exist!"),
                    @ApiResponse(responseCode = "422", description = "Role not found!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void addSkillToProfile(@HeaderParam("Authorization") String authorization,
                                  @Valid @NotNull(message = "Skill key must be provided!")
                                  @QueryParam("skillKey") String skillKey,
                                  @Valid @NotNull(message = "Skill level must be provided!")
                                  @QueryParam("skillLevel") String skillLevel,
                                  @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> addSkillToProfile(userEmail, skillKey, skillLevel), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable addSkillToProfile(String userEmail, String skillKey, String skillLevel) {
        return profileService.addSkillToProfile(userEmail, skillKey, skillLevel);
    }

    @POST
    @Path("/pdf")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Generate a CV based on the user's profile in PDF format.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return a PDF of the user's profile.",
                            content = @Content(mediaType = "application/octet-stream")),
                    @ApiResponse(responseCode = "404", description = "Profile not found for the given user!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    public void generatePDF(@HeaderParam("Authorization") String authorization, @Suspended AsyncResponse asyncResponse) {
        final String mediaType = "application/octet-stream";
        final String fileName = String.format("profile-pdf-%s.%s",
                LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm")),
                "pdf");

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        final ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> generatePDF(userEmail), executorService)
                .thenApplyAsync(fileContent -> asyncResponse.resume(
                                Response.ok(fileContent, mediaType)
                                        .header("content-disposition", "attachment; filename=" + fileName + "; filename*=UTF-8''" + fileName)
                                        .build()),
                        executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable generatePDF(String userEmail) throws ApiException {
        final Map<String, Object> templateContent = new HashMap<>();

        ProfileJSON profile = profileService.getProfile(userEmail);

        List<String> skillsInterestedIn = new ArrayList<>();

        Map<String, List<String>> skills = profile.getSkills().stream()
                .filter(categorySkillRelationJSON -> {
                    if ("INTERESTED".equals(categorySkillRelationJSON.getProfileSkill().getLevel())) {
                        skillsInterestedIn.add(categorySkillRelationJSON.getProfileSkill().getSkill().getName());
                    }

                    return !"INTERESTED".equals(categorySkillRelationJSON.getProfileSkill().getLevel());
                })
                .collect(Collectors.groupingBy(categorySkillRelationJSON
                                -> categorySkillRelationJSON.getCategory().getCategoryName(),
                        Collectors.mapping(categorySkillRelationJSON
                                -> categorySkillRelationJSON.getProfileSkill().getSkill().getName(), Collectors.toList())));

        templateContent.put("skills", skills);
        templateContent.put("skillsInterestedIn", skillsInterestedIn);
        templateContent.put("profile", profile);
        return templateService.generatePDF(Template.PROFILE_PDF, templateContent, language.getLocale());
    }

    @PUT
    @Path("/update")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns project updated.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectJSON.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "404", description = "Project not found for the given user!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @Anonymous
    public void updateProject(@HeaderParam("Authorization") String authorization,
                              @Valid @NotNull(message = "Project key must be provided!")
                              @QueryParam("Project Key") String projectKey,
                              ProjectInput projectInput,
                              @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String userEmail = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> updateProject(userEmail, projectKey, projectInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable updateProject(String userEmail, String projectKey, ProjectInput projectInput) {
        return profileService.updateProject(userEmail, projectKey, projectInput);
    }
}
