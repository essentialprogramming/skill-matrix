package com.api.output;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJSON implements Serializable {

    private String title;
    private String shortDescription;
    private String period;
    private String responsibilities;

}
