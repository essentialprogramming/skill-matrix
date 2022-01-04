package com.api.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "project_skill")
public class ProjectSkill {

    @EmbeddedId
    private ProjectSkillKey projectSkillId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @ManyToOne(cascade = CascadeType.MERGE)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", referencedColumnName = "id", nullable = false)
    private Skill skill;

    public ProjectSkill(Project project, Skill skill) {
        this.project = project;
        this.skill = skill;
        this.projectSkillId = new ProjectSkillKey(project.getId(), skill.getId());
    }
}
