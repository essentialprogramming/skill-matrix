package com.api.repository;


import com.api.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    List<Skill> findAllBySkillKeyIn(List<String> keys);
    Optional<Skill> findByName(String name);

    Boolean existsByNameIgnoreCase(String name);

    @Modifying
    @Query("UPDATE Skill s SET s.name = :newName WHERE s.skillKey = :skillKey")
    void updateName(@Param("skillKey") String skillKey, @Param("newName") String newName);

    @Modifying
    @Query("DELETE FROM Skill s where s.name = :name")
    void deleteSkills(@Param("name") String name);

    Optional<Skill> findByNameIgnoreCase(String name);

    Optional<Skill> findBySkillKey(String skillKey);
}
