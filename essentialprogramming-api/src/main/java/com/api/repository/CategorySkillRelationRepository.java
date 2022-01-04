package com.api.repository;

import com.api.entities.CategorySkillRelation;
import com.api.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategorySkillRelationRepository extends JpaRepository<CategorySkillRelation, Integer>, JpaSpecificationExecutor<CategorySkillRelation>, SkillRepositoryCustom{

      @Modifying
      @Query("DELETE FROM CategorySkillRelation csr WHERE csr.skill IN (SELECT s.id FROM Skill s WHERE s.name = :skillName)")
      void deleteSkills(@Param("skillName") String skillName);

      @Query("FROM CategorySkillRelation csr WHERE csr.skill.skillKey IN ?1")
      List<CategorySkillRelation> findAllBySkillKeyIn(List<String> keys);

      @Query("FROM CategorySkillRelation csr WHERE csr.skill IN (SELECT ps.skill FROM ProfileSkill ps WHERE ps.profile = ?1)")
      List<CategorySkillRelation> findAllByProfile(Profile profile);

      @Query("FROM CategorySkillRelation csr WHERE csr.skill.skillKey = :skillKey")
      Optional<CategorySkillRelation> findBySkillKey(@Param("skillKey") String skillKey);
}