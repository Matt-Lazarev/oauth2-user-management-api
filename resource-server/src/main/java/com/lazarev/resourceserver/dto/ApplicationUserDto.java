package com.lazarev.resourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<String> roles;
}
