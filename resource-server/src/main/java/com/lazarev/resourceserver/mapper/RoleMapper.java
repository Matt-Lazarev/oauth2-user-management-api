package com.lazarev.resourceserver.mapper;

import com.lazarev.resourceserver.dto.RoleDto;
import com.lazarev.resourceserver.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper {

    public RoleDto mapToDto(Role role){
        return new RoleDto(role.getId(), role.getName());
    }

    public Role mapToObject(RoleDto roleDto){
        Role role = new Role();
        role.setId(roleDto.getId());
        role.setName(roleDto.getName());
        return role;
    }

    public void copyFields(Role role, RoleDto dto) {
        if(dto.getId() != null){
            role.setId(dto.getId());
        }

        if(dto.getName() != null){
            role.setName(dto.getName());
        }
    }
}
