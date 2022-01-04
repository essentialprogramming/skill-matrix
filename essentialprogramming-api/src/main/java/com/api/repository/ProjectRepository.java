package com.api.repository;

import com.api.entities.Profile;
import com.api.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findAllByProfile(Profile profile);
    List<Project> findAllByProjectKeyIn(List<String> keys);

    Optional<Project> findByProjectKey(String projectKey);
}
