package com.api.service;

import com.api.entities.*;
import com.api.mapper.ProfileMapper;
import com.api.mapper.ProjectMapper;
import com.api.model.*;
import com.api.output.ProfileJSON;
import com.api.output.ProjectJSON;
import com.api.output.SimpleProfileJSON;
import com.api.repository.*;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.util.exceptions.ValidationException;
import com.util.web.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CategorySkillRelationRepository categorySkillRelationRepository;
    private final ProjectSkillRepository projectSkillRepository;

    @Transactional
    public ProfileJSON createProfile(String userEmail, ProfileInput profileInput) {
        if (profileRepository.existsByEmail(userEmail)) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Profile is already created!");
        }
        profileInput.setRole(profileInput.getRole().toUpperCase());
        try {
            Role.valueOf(profileInput.getRole());
        } catch (IllegalArgumentException e) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Role not found!");
        }

        profileInput.getSkillList().forEach(
                profileSkillInput -> {
                    profileSkillInput.setSkillLevel(profileSkillInput.getSkillLevel().toUpperCase());
                    try {
                        SkillLevel.valueOf(profileSkillInput.getSkillLevel());
                    } catch (IllegalArgumentException e) {
                        throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, profileSkillInput.getSkillLevel() + " does not exist!");
                    }
                }
        );

        Profile profile = ProfileMapper.inputToProfile(profileInput);

        List<Project> projects = new ArrayList<>();

        if (profileInput.getProjectList() != null) {
            projects = profileInput.getProjectList().stream().map(input -> {
                Project project = ProjectMapper.inputToProject(input);
                project.setProfile(profile);
                return project;
            }).collect(Collectors.toList());
        }

        profile.setProjects(projects);
        profile.setEmail(userEmail);
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User does not exist!"));
        user.setProfile(profile);

        Map<CategorySkillRelation, String> skillsWithLevel = new HashMap<>();
        if (profileInput.getSkillList() != null) {

            //skillKey, skillLevel
            Map<String, String> skillMap = profileInput.getSkillList()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    ProfileSkillInput::getSkillKey, ProfileSkillInput::getSkillLevel));

            skillMap.keySet().forEach(skillKey -> {
                Skill skillToAdd = skillRepository.findBySkillKey(skillKey)
                        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "The skill with key " + skillKey + " does not exist"));

                ProfileSkill profileSkill = new ProfileSkill(profile, skillToAdd, SkillLevel.valueOf(skillMap.get(skillKey)));
                profileSkillRepository.save(profileSkill);

                skillsWithLevel.put(categorySkillRelationRepository.findBySkillKey(skillKey).get(), skillMap.get(skillKey));

            });
        }

        profile.setSkillsWithLevel(skillsWithLevel);

        return ProfileMapper.entityToJSON(profile);
    }

    @Transactional
    public ProfileJSON getProfile(String userEmail) {

        Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Profile not found for the given user!"));

        Map<CategorySkillRelation, String> skillsWithLevel = new HashMap<>();

        List<Project> projects = projectRepository.findAllByProfile(profile);

        categorySkillRelationRepository.findAllByProfile(profile)
                .forEach(categorySkillRelation ->
                        skillsWithLevel.put(categorySkillRelation,
                                profileSkillRepository.findLevelBySkillAndProfile(categorySkillRelation.getSkill(), profile).get()
                ));

        profile.setProjects(projects);
        profile.setSkillsWithLevel(skillsWithLevel);

        return ProfileMapper.entityToJSON(profile);
    }

    public SimpleProfileJSON updateProfile(String userEmail, SimpleProfileInput newProfile) {

        Profile existingProfile = profileRepository.findByEmail(userEmail)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Profile not found for the given user!"));

        if (newProfile.getProfilePicture() != null) {
            existingProfile.setProfilePicture(newProfile.getProfilePicture());
        }
        if (newProfile.getFirstname() != null) {
            existingProfile.setFirstname(newProfile.getFirstname());
        }
        if (newProfile.getLastname() != null) {
            existingProfile.setLastname(newProfile.getLastname());
        }
        if (newProfile.getPhone() != null) {
            existingProfile.setPhone(newProfile.getPhone());
        }
        if (newProfile.getEducation() != null) {
            existingProfile.setEducation(newProfile.getEducation());
        }
        if (newProfile.getSummary() != null) {
            existingProfile.setSummary(newProfile.getSummary());
        }
        if (newProfile.getRole() != null) {
            existingProfile.setRole(Role.valueOf(newProfile.getRole()));
        }
        if (newProfile.getSpokenLanguages() != null) {
            existingProfile.setSpokenLanguages(newProfile.getSpokenLanguages());
        }

        profileRepository.save(existingProfile);

        return ProfileMapper.entityToSimpleJSON(existingProfile);
    }

    @Transactional
    public JsonResponse addProjectToProfile(String userEmail, ProjectInput projectInput) {
        Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Profile not found for the given email!"));

        Project project = ProjectMapper.inputToEntity(projectInput);
        String projectKey = NanoIdUtils.randomNanoId();
        project.setProjectKey(projectKey);

        if (!profile.getProjects().contains(project)) {
            profile.addProject(project);
        } else {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Project is already added to this profile!");
        }

        projectRepository.save(project);

        return new JsonResponse()
                .with("status", "ok")
                .with("message", "Project successfully added to the user with the email: " + userEmail)
                .with("projectKey", projectKey)
                .done();
    }

    @Transactional
    public JsonResponse addSkillToProject(String userEmail, String projectKey, String skillKey) {

        final Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User has no associated profile!"));

        final Skill skill = skillRepository.findBySkillKey(skillKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Skill not found!"));

        if (!profileSkillRepository.existsByProfileAndSkill(profile, skill)) {
            throw new ValidationException("The skill was not found in the skills list of the user's profile",
                    "Ensure that the skill is in the skills list of the user's profile!");
        }

        final Project project = projectRepository.findByProjectKey(projectKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Project not found!"));

        ProjectSkill projectSkill = new ProjectSkill(project, skill);
        projectSkillRepository.save(projectSkill);

        return new JsonResponse()
                .with("status", "ok")
                .with("message", "Skill successfully added to the project!")
                .done();
    }

    @Transactional
    public JsonResponse addSkillToProfile(String userEmail, String skillKey, String skillLevel) {
        final Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Profile not found for the given email!"));

        final Skill skill = skillRepository.findBySkillKey(skillKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Skill not found!"));

        skillLevel = skillLevel.toUpperCase();

        try {
            SkillLevel.valueOf(skillLevel);
        } catch (IllegalArgumentException e) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, skillLevel + " does not exist!");
        }

        ProfileSkill profileSkill = new ProfileSkill(profile, skill, SkillLevel.valueOf(skillLevel));
        profileSkillRepository.save(profileSkill);

        return new JsonResponse()
                .with("status", "ok")
                .with("message", "Skill successfully added to the user with the email " + userEmail)
                .done();
    }

    @Transactional
    public ProjectJSON updateProject(String userEmail, String projectKey, ProjectInput newProject) {
        Project existingProject = projectRepository.findByProjectKey(projectKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Project not found!"));

        if (newProject.getTitle() != null)
            existingProject.setTitle(newProject.getTitle());

        if (newProject.getShortDescription() != null)
            existingProject.setShortDescription(newProject.getShortDescription());

        if (newProject.getPeriod() != null)
            existingProject.setPeriod(newProject.getPeriod());

        if (newProject.getResponsibilities() != null)
            existingProject.setResponsibilities(newProject.getResponsibilities());

        projectRepository.save(existingProject);

        return ProjectMapper.entityToJSON(existingProject);
    }
}
