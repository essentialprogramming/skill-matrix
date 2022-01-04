package com.api.entities;

import com.api.model.Role;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "education")
    private String education;

    @Column(name = "summary", length = 250)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.PERSIST)
    private List<Project> projects;

    @Transient
    private Map<CategorySkillRelation, String> skillsWithLevel;

    @ElementCollection
    @CollectionTable(name = "spoken_languages",
            joinColumns = {@JoinColumn(name = "profile_id", referencedColumnName = "id")})
    @Column(name = "spoken_language")
    private List<String> spokenLanguages;

    public boolean addProject(Project project) {
        if (!projects.contains(project)) {
            this.projects.add(project);
            if (project.getProfile() == null) {
                project.setProfile(this);
            }
            return true;
        }
        return false;
    }
}
