package com.lazarev.resourceserver.repository;

import com.lazarev.resourceserver.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    Integer deleteApplicationUserById(Long id);

    Optional<ApplicationUser> findByEmail(String name);
}
