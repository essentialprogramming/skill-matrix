package com.api.mapper;

import com.api.entities.SkillCategory;
import com.api.output.SkillCategoryJSON;

public class SkillCategoryMapper {

    public static SkillCategoryJSON entityToJSON(SkillCategory skillCategory) {
        return SkillCategoryJSON.builder()
                .categoryKey(skillCategory.getCategoryKey())
                .categoryName(skillCategory.getCategoryName())
                .build();
    }
}
