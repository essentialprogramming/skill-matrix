package com.api.repository;

import com.api.entities.Skill;
import com.api.entities.SuggestedSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuggestedSkillRepository extends JpaRepository<SuggestedSkill, Integer> {

    Optional<SuggestedSkill> findByName(String name);

    Optional<SuggestedSkill> findBySuggestedSkillKey(String key);

    Optional<Skill> findByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCase(String name);

}
