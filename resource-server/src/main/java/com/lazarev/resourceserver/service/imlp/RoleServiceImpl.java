package com.lazarev.resourceserver.service.imlp;

import com.lazarev.resourceserver.dto.RoleDto;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.exception.custom.RoleNotFoundException;
import com.lazarev.resourceserver.mapper.RoleMapper;
import com.lazarev.resourceserver.repository.RoleRepository;
import com.lazarev.resourceserver.service.abstracts.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Transactional
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void saveRole(RoleDto dto) {
        Role role = roleMapper.mapToObject(dto);
        roleRepository.save(role);
    }

    @Override
    public void updateRole(Long id, RoleDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(
                String.format("Role with id = '%d' not found", id)));

        roleMapper.copyFields(role, dto);
        roleRepository.save(role);
    }

    @Override
    public Integer deleteRoleById(Long id) {
        return roleRepository.deleteRoleById(id);
    }
}
