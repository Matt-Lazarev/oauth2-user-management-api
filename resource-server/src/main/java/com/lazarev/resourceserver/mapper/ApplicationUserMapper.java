package com.lazarev.resourceserver.mapper;

import com.lazarev.resourceserver.dto.ApplicationUserDto;
import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationUserMapper {

    public ApplicationUserDto mapToDto(ApplicationUser user){
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return new ApplicationUserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                roles
        );
    }

    public ApplicationUser mapToObject(ApplicationUserDto dto){
        ApplicationUser user = new ApplicationUser();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(dto.getPassword());

        return user;
    }

    public void copyFields(ApplicationUser user, ApplicationUserDto userDto) {
        if(userDto.getId() != null){
            user.setId(userDto.getId());
        }

        if(userDto.getEmail() != null){
            user.setEmail(userDto.getEmail());
        }

        if(userDto.getFirstName() != null){
            user.setFirstName(userDto.getFirstName());
        }

        if(userDto.getLastName() != null){
            user.setLastName(userDto.getLastName());
        }

        if(userDto.getPassword() != null){
            user.setPassword(userDto.getPassword());
        }
    }
}
