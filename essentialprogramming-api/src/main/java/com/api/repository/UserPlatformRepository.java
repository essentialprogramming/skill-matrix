package com.api.repository;

import com.api.entities.UserPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlatformRepository extends JpaRepository<UserPlatform, Integer> {

}
