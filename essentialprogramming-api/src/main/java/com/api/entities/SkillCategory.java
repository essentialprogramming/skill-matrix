package com.api.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill_category")
public class SkillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "category_key", nullable = false, unique = true)
    private String categoryKey;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;
}
