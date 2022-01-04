package com.api.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSkillJSON implements Serializable {

    private SkillJSON skill;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String level;

    public ProfileSkillJSON(SkillJSON skill) {
        this.skill = skill;
    }
}
