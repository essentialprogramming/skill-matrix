package com.api.output;

import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillJSON implements Serializable {

    private String skillKey;
    private String name;

}
