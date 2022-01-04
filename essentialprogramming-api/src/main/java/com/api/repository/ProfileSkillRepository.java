package com.api.repository;

import com.api.entities.Profile;
import com.api.entities.ProfileSkill;
import com.api.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Integer>, JpaSpecificationExecutor<ProfileSkill>, ProfileSkillRepositoryCustom {

    @Query("FROM ProfileSkill pS WHERE pS.profile.email = ?1")
    List<ProfileSkill> findAllSkillsForProfile(String userEmail);

    @Query("SELECT pS.skill FROM ProfileSkill pS WHERE pS.skill = ?1")
    Optional<Skill> findSkillByProfile(Skill skill);

    @Query("SELECT pS.skillLevel FROM ProfileSkill pS WHERE pS.skill = ?1 AND pS.profile = ?2")
    Optional<String> findLevelBySkillAndProfile(Skill skill, Profile profile);
}
