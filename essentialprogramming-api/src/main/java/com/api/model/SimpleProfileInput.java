package com.api.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleProfileInput {

    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String education;
    private String summary;
    private String role;
    private List<String> spokenLanguages;
}
