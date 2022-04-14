package com.lazarev.resourceserver.repository;

import com.lazarev.resourceserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    int deleteRoleById(Long id);
}
