package com.lazarev.oauthserver.repository;

import com.lazarev.oauthserver.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByEmail(String email);
}
