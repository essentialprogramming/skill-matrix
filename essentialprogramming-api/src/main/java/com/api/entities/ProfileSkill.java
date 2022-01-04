package com.api.entities;

import com.api.model.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile_skill")
public class ProfileSkill {

    @EmbeddedId
    private ProfileSkillKey profileSkillId;

    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", referencedColumnName = "id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false)
    private SkillLevel skillLevel;

    public ProfileSkill(Profile profile, Skill skill, SkillLevel skillLevel) {
        this.profile = profile;
        this.skill = skill;
        this.skillLevel = skillLevel;
        this.profileSkillId = new ProfileSkillKey(profile.getId(), skill.getId());
    }
}
