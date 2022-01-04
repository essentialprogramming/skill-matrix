package com.api.repository;

import com.api.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    @Query("FROM Profile p LEFT JOIN FETCH p.spokenLanguages WHERE p.email = ?1")
    Optional<Profile> findByEmail(String email);

    Boolean existsByEmail(String email);

}
