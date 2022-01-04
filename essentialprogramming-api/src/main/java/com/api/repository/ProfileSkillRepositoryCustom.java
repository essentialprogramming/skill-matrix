package com.api.repository;

import com.api.entities.ProfileSkill;
import com.api.model.ProfileSkillSearchCriteria;

import java.util.List;

public interface ProfileSkillRepositoryCustom {

    List<ProfileSkill> searchProfileSkills(ProfileSkillSearchCriteria profileSkillSearchCriteria, String userEmail);
}
