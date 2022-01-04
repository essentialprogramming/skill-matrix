package com.api.mapper;

import com.api.entities.Profile;
import com.api.model.ProfileInput;
import com.api.model.Role;
import com.api.output.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileMapper {

    public static Profile inputToProfile(ProfileInput profileInput) {

        return Profile.builder()
                .profilePicture(profileInput.getProfilePicture())
                .firstname(profileInput.getFirstname())
                .lastname(profileInput.getLastname())
                .phone(profileInput.getPhone())
                .education(profileInput.getEducation())
                .summary(profileInput.getSummary())
                .role(Role.valueOf(profileInput.getRole()))
                .spokenLanguages(profileInput.getSpokenLanguages())
                .build();
    }

    public static ProfileJSON entityToJSON(Profile profile) {

        List<CategorySkillRelationJSON> categorySkillRelationJSONList = new ArrayList<>();

        profile.getSkillsWithLevel().keySet().forEach(categorySkillRelation ->
                        categorySkillRelationJSONList.add(
                CategorySkillRelationJSON.builder()
                        .profileSkill(ProfileSkillJSON.builder()
                                .skill(SkillMapper.entityToJSON(categorySkillRelation.getSkill()))
                                .level(profile.getSkillsWithLevel().get(categorySkillRelation))
                                .build())
                        .category(SkillCategoryJSON.builder()
                                .categoryKey(categorySkillRelation.getCategory().getCategoryKey())
                                .categoryName(categorySkillRelation.getCategory().getCategoryName())
                                .build())
                        .build()));

        return ProfileJSON.builder()
                .profilePicture(profile.getProfilePicture())
                .firstName(profile.getFirstname())
                .lastName(profile.getLastname())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .education(profile.getEducation())
                .summary(profile.getSummary())
                .role(profile.getRole().toString().replace("_", " "))
                .projects(profile.getProjects().stream().map(ProjectMapper::entityToJSON).collect(Collectors.toList()))
                .spokenLanguages(profile.getSpokenLanguages())
                .skills(categorySkillRelationJSONList)
                .build();

    }

    public static SimpleProfileJSON entityToSimpleJSON(Profile profile) {

        return SimpleProfileJSON.builder()
                .profilePicture(profile.getProfilePicture())
                .firstName(profile.getFirstname())
                .lastName(profile.getLastname())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .education(profile.getEducation())
                .summary(profile.getSummary())
                .role(profile.getRole().toString())
                .spokenLanguages(profile.getSpokenLanguages())
                .build();
    }
}
