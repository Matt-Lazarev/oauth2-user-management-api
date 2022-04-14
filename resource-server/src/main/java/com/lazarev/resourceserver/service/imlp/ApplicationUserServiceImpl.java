package com.lazarev.resourceserver.service.imlp;

import com.lazarev.resourceserver.dto.ApplicationUserDto;
import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.exception.custom.ApplicationUserNotFoundException;
import com.lazarev.resourceserver.mapper.ApplicationUserMapper;
import com.lazarev.resourceserver.repository.ApplicationUserRepository;
import com.lazarev.resourceserver.repository.RoleRepository;
import com.lazarev.resourceserver.service.abstracts.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Transactional
@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final RoleRepository roleRepository;
    private final ApplicationUserMapper applicationUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(ApplicationUserDto applicationUserDto) {
        List<Role> roles = new ArrayList<>();
        for(String srtRole: applicationUserDto.getRoles()){
            Optional<Role> roleOptional = roleRepository.findByName(srtRole);
            roleOptional.ifPresent(roles::add);
        }

        ApplicationUser user = applicationUserMapper.mapToObject(applicationUserDto);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        applicationUserRepository.save(user);
    }

    @Override
    public List<ApplicationUser> getAllApplicationUsers() {
        return applicationUserRepository.findAll();
    }

    @Override
    public ApplicationUser getApplicationUserById(Long id) {
        return applicationUserRepository.findById(id)
                .orElseThrow(() -> new ApplicationUserNotFoundException(
                            String.format("User with id = '%d' not found", id)));
    }

    @Override
    public void updateUser(ApplicationUserDto userDto, Long id) {
        ApplicationUser user = applicationUserRepository.findById(id)
                .orElseThrow(() -> new ApplicationUserNotFoundException(
                        String.format("User with id = '%d' not found", id)));

        applicationUserMapper.copyFields(user, userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        List<Role> roles = new ArrayList<>();
        for(String srtRole: userDto.getRoles()){
            Optional<Role> roleOptional = roleRepository.findByName(srtRole);
            roleOptional.ifPresent(roles::add);
        }

        user.setRoles(roles);
        applicationUserRepository.save(user);
    }

    @Override
    public Integer deleteUserById(Long id) {
        return applicationUserRepository.deleteApplicationUserById(id);
    }

    @Override
    public String[] getUserRoles(String email) {
        ApplicationUser user = applicationUserRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationUserNotFoundException(
                        String.format("User with email = '%s' not found", email)));
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList())
                .toArray(String[]::new);
    }
}