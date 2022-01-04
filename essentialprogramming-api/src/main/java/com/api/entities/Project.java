package com.api.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "project_key")
    private String projectKey;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "short_description", length = 250, nullable = false)
    private String shortDescription;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "responsibilities", length = 500, nullable = false)
    private String responsibilities;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;
}
