package com.word.userservice.repository;

import com.word.userservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository <UserProfile, Long> {
    Optional<UserProfile> findByAuthUserId(String authUserId);
}
