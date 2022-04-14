package com.lazarev.resourceserver.service.abstracts;

import com.lazarev.resourceserver.dto.RoleDto;
import com.lazarev.resourceserver.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();

    void saveRole(RoleDto dto);

    void updateRole(Long id, RoleDto dto);

    Integer deleteRoleById(Long id);
}
