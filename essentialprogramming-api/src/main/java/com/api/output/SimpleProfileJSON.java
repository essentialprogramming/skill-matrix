package com.api.output;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleProfileJSON implements Serializable {

    private String profilePicture;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String education;
    private String summary;
    private String role;
    private List<String> spokenLanguages;
}
