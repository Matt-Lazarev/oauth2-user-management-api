package com.lazarev.resourceserver.service.abstracts;


import com.lazarev.resourceserver.dto.ApplicationUserDto;
import com.lazarev.resourceserver.entity.ApplicationUser;

import java.util.List;

public interface ApplicationUserService {
    List<ApplicationUser> getAllApplicationUsers();

    ApplicationUser getApplicationUserById(Long id);

    String[] getUserRoles(String email);

    void saveUser(ApplicationUserDto applicationUserDto);

    void updateUser(ApplicationUserDto userDto, Long id);

    Integer deleteUserById(Long id);
}
