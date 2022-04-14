package com.lazarev.resourceserver.controller;

import com.lazarev.resourceserver.dto.RoleDto;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.service.abstracts.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roleService.getAllRoles());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> saveNewRole(@RequestBody RoleDto dto){
        roleService.saveRole(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleDto dto){
        roleService.updateRole(id, dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> deleteRole(@PathVariable Long id){
        int rolesUpdated = roleService.deleteRoleById(id);

        if(rolesUpdated > 0){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        }
        else {
            String message = String.format("Role with id = '%d' not found", id);
            Map<String, String> error = Map.of(HttpStatus.NOT_FOUND.name(), message);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);
        }


    }
}
