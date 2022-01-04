package com.api.mapper;

import com.api.entities.CategorySkillRelation;
import com.api.output.CategorySkillRelationJSON;
import com.api.output.ProfileSkillJSON;

public class CategorySkillRelationMapper {

    public static CategorySkillRelationJSON entityToJSON(CategorySkillRelation categorySkillRelation) {
        return CategorySkillRelationJSON.builder()
                .profileSkill(new ProfileSkillJSON(SkillMapper.entityToJSON(categorySkillRelation.getSkill())))
                .category(SkillCategoryMapper.entityToJSON(categorySkillRelation.getCategory()))
                .build();
    }
}
