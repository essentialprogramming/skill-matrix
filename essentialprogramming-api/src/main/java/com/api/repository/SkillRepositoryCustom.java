package com.api.repository;

import com.api.entities.CategorySkillRelation;
import com.api.model.SkillSearchCriteria;

import java.util.List;

public interface SkillRepositoryCustom {

    List<CategorySkillRelation> searchSkills(SkillSearchCriteria skillSearchCriteria);
}
