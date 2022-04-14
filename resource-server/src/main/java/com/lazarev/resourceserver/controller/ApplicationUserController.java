package com.lazarev.resourceserver.controller;

import com.lazarev.resourceserver.dto.ApplicationUserDto;
import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.service.abstracts.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<List<ApplicationUser>> getAllUsers(){
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(applicationUserService.getAllApplicationUsers());
    }

    @GetMapping("/info/{email}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER') and hasAuthority('SCOPE_openid')")
    public ResponseEntity<String[]> getUserAuthorities(@PathVariable String email){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(applicationUserService.getUserRoles(email));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        ApplicationUser user = applicationUserService.getApplicationUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> saveUser(@RequestBody ApplicationUserDto dto){
        applicationUserService.saveUser(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody ApplicationUserDto dto){
        applicationUserService.updateUser(dto, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') and hasAuthority('SCOPE_api.read')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        int usersUpdated = applicationUserService.deleteUserById(id);

        if(usersUpdated > 0){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        }
        else {
            String message = String.format("User with id = '%d' not found", id);
            Map<String, String> error = Map.of(HttpStatus.NOT_FOUND.name(), message);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);
        }
    }
}
