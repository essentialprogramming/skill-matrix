package com.util;

import com.api.model.*;

import java.security.SecureRandom;
import java.util.ArrayList;

public class TestEntityGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static UserInput generateUserInput() {
        return UserInput.builder()
                .firstName("GeneratedFirstName")
                .lastName("GeneratedLastName")
                .email("email" + secureRandom.nextInt() + "@mail.com")
                .phone("0789456123")
                .build();
    }

    public static ProfileInput generateProfileInput(Role role) {
        return ProfileInput.builder()
                .profilePicture("Picture" + secureRandom.nextInt())
                .firstname("Name" + secureRandom.nextInt())
                .lastname("lastName" + secureRandom.nextInt())
                .phone("0789456123")
                .summary("summary" + secureRandom.nextInt())
                .role(role.name())
                .projectList(new ArrayList<>())
                .skillList(new ArrayList<>())
                .spokenLanguages(new ArrayList<>())
                .build();
    }

    public static ProjectInput generateProjectInput() {

        return ProjectInput.builder()
                .title("title" + secureRandom.nextInt())
                .shortDescription("description" + secureRandom.nextInt())
                .period("period" + secureRandom.nextInt())
                .responsibilities("responsibilities" + secureRandom.nextInt())
                .build();
    }
}
