package com.word.userservice.repository;

import com.word.userservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByPublicId(String publicId);
    boolean existsByEmail(String email);
    boolean existsByPublicId(String publicId);
}
