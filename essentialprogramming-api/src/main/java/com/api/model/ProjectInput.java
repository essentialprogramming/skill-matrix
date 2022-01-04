package com.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectInput {

    private String title;
    private String shortDescription;
    private String period;
    private String responsibilities;
}
