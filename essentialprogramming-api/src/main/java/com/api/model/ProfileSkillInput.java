package com.api.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSkillInput {

    private String skillKey;
    private String skillLevel;
}
