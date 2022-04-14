package com.lazarev.resourceserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lazarev.resourceserver.dto.ApplicationUserDto;
import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.exception.custom.ApplicationUserNotFoundException;
import com.lazarev.resourceserver.service.abstracts.ApplicationUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = ApplicationUserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ApplicationUserControllerTest {

    private static final String resourceUrl = "http://localhost:8090/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    public void init(){
        objectWriter = new ObjectMapper().writer();
    }

    @MockBean
    private ApplicationUserService applicationUserService;

    @Test
    @DisplayName(
            "Given - " +
            "When - getAllUsers request is performed " +
            "Then - List<ApplicationUser>"
    )
    public void canGetAllUsersTest() throws Exception {
        //given

        List<ApplicationUser> users = List.of(
                new ApplicationUser(
                        1L,
                        "Matt",
                        "Lazarev",
                        "m@mail.ru",
                        "123",
                        List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER"))),
                new ApplicationUser(
                        1L,
                        "Bob",
                        "Tyson",
                        "b@mail.ru",
                        "123",
                        List.of(new Role(2L, "ROLE_USER")))
        );

        //when
        when(applicationUserService.getAllApplicationUsers())
                .thenReturn(users);


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(resourceUrl))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = mvcResult.getResponse().getContentAsString();
        then(content).isEqualTo(objectWriter.writeValueAsString(users));
    }

    @Test
    @DisplayName(
             "Given - ApplicationUser roles and email " +
             "When - getUserAuthorities request is performed " +
             "Then - result contains all current user roles"
    )
    public void canGetUserAuthoritiesTest_success() throws Exception {
        //given
        String[] userRoles = {"ROLE_ADMIN", "ROLE_USER"};
        String email = "m@mail.ru";

        //when
        when(applicationUserService.getUserRoles(email))
                .thenReturn(userRoles);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(resourceUrl + "/info/" + email))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = mvcResult.getResponse().getContentAsString();
        then(content).contains("ROLE_ADMIN");
        then(content).contains("ROLE_USER");
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser roles and email " +
            "When - getUserAuthorities request is performed " +
            "Then - throws ApplicationUserNotFoundException & notFound"
    )
    public void canGetUserAuthoritiesTest_fail() throws Exception {
        //given
        String[] userRoles = {"ROLE_ADMIN", "ROLE_USER"};
        String email = "m@mail.ru";

        //when
        when(applicationUserService.getUserRoles(email))
                .thenThrow(ApplicationUserNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                .get(resourceUrl + "/info/" + email))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - getUserById request is performed " +
            "Then - result contains the same ApplicationUser"
    )
    public void canGetUserByIdTest_success() throws Exception {
        //given
        ApplicationUser applicationUser = new ApplicationUser(
                    1L,
                    "Matt",
                    "Lazarev",
                    "m@mail.ru",
                    "123",
                    List.of(new Role(1L, "ROLE_ADMIN"),
                            new Role(2L, "ROLE_USER")));

        //when
        when(applicationUserService.getApplicationUserById(applicationUser.getId()))
                .thenReturn(applicationUser);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(resourceUrl + "/" + applicationUser.getId()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = mvcResult.getResponse().getContentAsString();
        then(content).isEqualTo(objectWriter.writeValueAsString(applicationUser));
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - getUserById request is performed " +
            "Then - throws ApplicationUserNotFoundException & notFound"
    )
    public void canGetUserByIdTest_fail() throws Exception {
        //given
        ApplicationUser applicationUser = new ApplicationUser(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of(new Role(1L, "ROLE_ADMIN"),
                        new Role(2L, "ROLE_USER")));

        //when
        when(applicationUserService.getApplicationUserById(applicationUser.getId()))
                .thenThrow(ApplicationUserNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                .get(resourceUrl + "/" + applicationUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUserDto " +
            "When - saveUser request is performed " +
            "Then - result is OK"
    )
    public void canSaveUserTest() throws Exception {
        //given
        ApplicationUserDto dto = new ApplicationUserDto(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of("ROLE_ADMIN", "ROLE_USER"));

        willDoNothing()
                .given(applicationUserService)
                .saveUser(dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                .post(resourceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUserDto " +
            "When - updateUser request is performed " +
            "Then - result is OK"
    )
    public void canUpdateUserTest_success() throws Exception {
        //given
        ApplicationUserDto dto = new ApplicationUserDto(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of("ROLE_ADMIN", "ROLE_USER"));

        willDoNothing()
                .given(applicationUserService)
                .updateUser(dto, dto.getId());

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                .put(resourceUrl + "/" + dto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUserDto " +
            "When - updateUser request is performed " +
            "Then - throws ApplicationUserNotFoundException & notFound"
    )
    public void canUpdateUserTest_fails() throws Exception {
        //given
        ApplicationUserDto dto = new ApplicationUserDto(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of("ROLE_ADMIN", "ROLE_USER"));

        willThrow(ApplicationUserNotFoundException.class)
                .given(applicationUserService)
                .updateUser(any(), eq(dto.getId()));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                .put(resourceUrl + "/" + dto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - deleteUser request is performed " +
            "Then - result is OK"
    )
    public void canDeleteUserTest_success() throws Exception {
        //given
        Long id = 1L;

        //when
        when(applicationUserService.deleteUserById(id))
                .thenReturn(1);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                .delete(resourceUrl + "/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - deleteUser request is performed " +
            "Then - throws ApplicationUserNotFoundException & notFound"
    )
    public void canDeleteUserTest_fail() throws Exception {
        //given
        Long id = 1L;

        //when
        when(applicationUserService.deleteUserById(id))
                .thenThrow(ApplicationUserNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                .delete(resourceUrl + "/" + id))
                .andExpect(status().isNotFound());
    }
}