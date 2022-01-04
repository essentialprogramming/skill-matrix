package com.api.output;

import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySkillRelationJSON implements Serializable {

    private ProfileSkillJSON profileSkill;
    private SkillCategoryJSON category;
}
