package com.lazarev.userclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApplicationUser {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Role> roles;
}
