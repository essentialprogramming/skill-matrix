package com.api.output;

import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillCategoryJSON implements Serializable {

    private String categoryKey;
    private String categoryName;
}
