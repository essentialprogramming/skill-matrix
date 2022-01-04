package com.api.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "category_skill_relation")
public class CategorySkillRelation {

    @EmbeddedId
    private CategorySkillRelationKey categorySkillRelationId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private SkillCategory category;

    @ManyToOne(cascade = CascadeType.MERGE)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", referencedColumnName = "id", nullable = false)
    private Skill skill;

    public CategorySkillRelation(SkillCategory category, Skill skill) {
        this.category = category;
        this.skill = skill;
        this.categorySkillRelationId = new CategorySkillRelationKey(category.getId(), skill.getId());
    }
}
