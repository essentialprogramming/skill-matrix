package com.api.service;

import com.api.entities.*;
import com.api.mapper.CategorySkillRelationMapper;
import com.api.mapper.ProfileSkillMapper;
import com.api.model.ProfileSkillSearchCriteria;

import com.api.model.EditSkillInput;
import com.api.model.SkillSearchCriteria;
import com.api.output.CategorySkillRelationJSON;
import com.api.output.ProfileSkillJSON;
import com.api.output.SkillJSON;
import com.api.repository.*;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.email.service.EmailManager;
import com.internationalization.EmailMessages;
import com.template.model.Template;
import com.util.web.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SkillService {

    private final SkillCategoryRepository skillCategoryRepository;
    private final UserRepository userRepository;
    private final SuggestedSkillRepository suggestedSkillRepository;
    private final SkillRepository skillRepository;
    private final CategorySkillRelationRepository categorySkillRelationRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final EmailManager emailManager;

    @Transactional
    public JsonResponse addSkillCategory(String name) {

        if (skillCategoryRepository.findByCategoryNameIgnoreCase(name).isPresent()) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "This skill category already exists");
        }
        String categoryKey = NanoIdUtils.randomNanoId();
        skillCategoryRepository.save(
                SkillCategory
                        .builder()
                        .categoryKey(categoryKey)
                        .categoryName(name)
                        .build());

        return new JsonResponse()
                .with("status", "created")
                .with("message", name + " skill category successfully added.")
                .with("categoryKey", categoryKey)
                .done();
    }


    @Transactional
    public JsonResponse suggestSkill(String email, String skillName, String categoryKey) {
        //check if already exists
        if (suggestedSkillRepository.existsByNameIgnoreCase(skillName)) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "This skill was already suggested");
        }
        if (skillRepository.existsByNameIgnoreCase(skillName)) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "This skill already exists");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User does not exist"));

        SkillCategory skillCategory = skillCategoryRepository.findByCategoryKey(categoryKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "This category does not exist"));

        suggestedSkillRepository.save(
                SuggestedSkill
                        .builder()
                        .suggestedSkillKey(NanoIdUtils.randomNanoId())
                        .name(skillName)
                        .category(skillCategory)
                        .user(user)
                        .build()
        );
        return new JsonResponse()
                .with("status", "ok")
                .with("message", skillName + " suggested to admin.")
                .done();
    }

    @Transactional
    public JsonResponse acceptSuggestSkill(String suggestedSkillKey, com.util.enums.Language language) {

        SuggestedSkill suggestedSkill = suggestedSkillRepository.findBySuggestedSkillKey(suggestedSkillKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "This skill suggestion does not exist"));


        addNewSkill(suggestedSkill.getName(), suggestedSkill.getCategory().getCategoryKey());

        suggestedSkillRepository.delete(suggestedSkill);

        Map<String, Object> templateKeysAndValues = new HashMap<>();
        templateKeysAndValues.put("suggestedSkillName", suggestedSkill.getName());
        templateKeysAndValues.put("fullName", suggestedSkill.getUser().getFullName());
        emailManager.send(suggestedSkill.getUser().getEmail(),
                EmailMessages.get("skill_suggestion.subject", language.getLocale()), Template.SKILL_SUGGESTION_ACCEPTED, templateKeysAndValues, language.getLocale());

        return new JsonResponse()
                .with("status", "created")
                .with("message", suggestedSkill.getName() + " skill suggestion added.")
                .done();
    }

    @Transactional
    public JsonResponse denySuggestSkill(String suggestedSkillKey, com.util.enums.Language language) {

        SuggestedSkill suggestedSkill = suggestedSkillRepository.findBySuggestedSkillKey(suggestedSkillKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "This skill suggestion does not exist"));

        suggestedSkillRepository.delete(suggestedSkill);

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("suggestedSkillName", suggestedSkill.getName());
        templateVariables.put("fullName", suggestedSkill.getUser().getFullName());
        emailManager.send(suggestedSkill.getUser().getEmail(),
                EmailMessages.get("skill_suggestion.subject", language.getLocale()), Template.SKILL_SUGGESTION_DECLINED, templateVariables, language.getLocale());

        return new JsonResponse()
                .with("status", "no content")
                .with("message", suggestedSkill.getName() + " skill suggestion denied.")
                .done();
    }

    @Transactional
    public SkillJSON addNewSkill(String skillName, String skillCategoryKey) {

        SkillCategory skillCategory = skillCategoryRepository.findByCategoryKey(skillCategoryKey)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Skill category not found!"));

        if (skillRepository.existsByNameIgnoreCase(skillName))
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "This skill already exist");

        String skillKey = NanoIdUtils.randomNanoId();

        CategorySkillRelation categorySkillRelation1 = new CategorySkillRelation(skillCategory,
                Skill.builder()
                        .skillKey(skillKey)
                        .name(skillName)
                        .build());

        categorySkillRelationRepository.save(categorySkillRelation1);


        return SkillJSON.builder()
                .name(skillName)
                .skillKey(skillKey)
                .build();
    }


    @Transactional
    public JsonResponse editSkill(EditSkillInput skillInput) {

        Skill skillToEdit = skillRepository.findBySkillKey(skillInput.getSkillToEditKey())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Skill not found!"));

        if (skillInput.getNewSkillName() != null) {
            skillRepository.updateName(skillInput.getSkillToEditKey(), skillInput.getNewSkillName());
        }

        if (skillInput.getCategoryToEditKey()!= null && skillInput.getCategoryToAssociateKey() != null) {

            SkillCategory skillCategoryToEdit = skillCategoryRepository.findByCategoryKey(skillInput.getCategoryToEditKey())
                    .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Skill category to edit does not exist!"));

            if (categorySkillRelationRepository.findBySkillKeyAndCategoryKey(skillInput.getSkillToEditKey(), skillInput.getCategoryToAssociateKey()).isPresent()) {
                throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Category already associated!");
            }
            SkillCategory skillCategoryToAssociate = skillCategoryRepository.findByCategoryKey(skillInput.getCategoryToAssociateKey())
                    .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Skill category to associate does not exist!"));

            categorySkillRelationRepository.updateCategory(skillCategoryToEdit, skillCategoryToAssociate, skillToEdit);
        }

        return new JsonResponse()
                .with("status", "no content")
                .with("message", "Skill successfully edited.")
                .done();
    }

    public List<CategorySkillRelationJSON> searchSkills(SkillSearchCriteria skillSearchCriteria) {
        List<CategorySkillRelation> skillsList;

        if (skillSearchCriteria.isEmpty()) {
            skillsList = categorySkillRelationRepository.findAll();
        } else {
            skillsList = categorySkillRelationRepository.searchSkills(skillSearchCriteria);
        }

        return skillsList.stream().map(CategorySkillRelationMapper::entityToJSON).collect(Collectors.toList());
    }

    public List<ProfileSkillJSON> searchProfileSkills(ProfileSkillSearchCriteria profileSkillSearchCriteria, String userEmail) {

        List<ProfileSkill> profileSkillsList;

        if (profileSkillSearchCriteria.isEmpty()) {
            profileSkillsList = profileSkillRepository.findAllSkillsForProfile(userEmail);
        } else {
            profileSkillsList = profileSkillRepository.searchProfileSkills(profileSkillSearchCriteria, userEmail);
        }

        return profileSkillsList.stream().map(ProfileSkillMapper::entityToJSON).collect(Collectors.toList());
    }
}
