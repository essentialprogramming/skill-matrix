package com.api.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suggested_skill")
public class SuggestedSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "suggested_skill_key", nullable = false)
    private String suggestedSkillKey;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private SkillCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
