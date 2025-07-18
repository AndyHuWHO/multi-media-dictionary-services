package com.word.authservice.repository;

import com.word.authservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByPublicId(String publicId);
    boolean existsByEmail(String email);
    boolean existsByPublicId(String publicId);
}
