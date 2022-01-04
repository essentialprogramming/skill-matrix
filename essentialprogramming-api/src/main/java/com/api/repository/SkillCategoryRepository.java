package com.api.repository;

import com.api.entities.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Integer> {

    Optional<SkillCategory> findByCategoryNameIgnoreCase(String name);

    Optional<SkillCategory> findByCategoryKey(String categoryKey);
}
