package com.api.model;

import lombok.*;

import javax.validation.constraints.NotNull;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditSkillInput {

    @NotNull(message = "The key of the skill to edit must not be null!")
    private String skillToEditKey;

    private String newSkillName;
    private String categoryToEditKey;
    private String categoryToAssociateKey;
}
