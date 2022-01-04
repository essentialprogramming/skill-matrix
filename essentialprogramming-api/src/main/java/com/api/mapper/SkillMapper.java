package com.api.mapper;

import com.api.entities.Skill;
import com.api.output.SkillJSON;

public class SkillMapper {

    public static SkillJSON entityToJSON(Skill skill) {
        return SkillJSON.builder()
                .skillKey(skill.getSkillKey())
                .name(skill.getName())
                .build();
    }
}
