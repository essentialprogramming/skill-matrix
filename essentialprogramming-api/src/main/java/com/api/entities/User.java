package com.api.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_language_id")
    private Language language;

    @Column(name = "validated")
    private boolean validated;

    @Column(name = "user_key")
    private String userKey;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "active")
    private boolean active;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_by")
    private Integer modifiedBy;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "password")
    private String password;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    public String getFullName() {
        return Optional.ofNullable(firstName).orElse("") + " " + Optional.ofNullable(lastName).orElse("");
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserPlatform> userPlatformList = new ArrayList<>();
}
