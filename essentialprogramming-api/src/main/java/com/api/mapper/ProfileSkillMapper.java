package com.api.mapper;

import com.api.entities.ProfileSkill;
import com.api.output.ProfileSkillJSON;

public class ProfileSkillMapper {

    public static ProfileSkillJSON entityToJSON(ProfileSkill profileSkill) {
        return ProfileSkillJSON.builder()
                .skill(SkillMapper.entityToJSON(profileSkill.getSkill()))
                .build();
    }
}
