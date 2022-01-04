package com.api.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProfileSkillKey implements Serializable {

    @Column(name = "profile_id")
    private int profileId;

    @Column(name = "skill_id")
    private int skillId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileSkillKey that = (ProfileSkillKey) o;
        return profileId == that.profileId && skillId == that.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, skillId);
    }
}
