package com.lazarev.resourceserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lazarev.resourceserver.dto.RoleDto;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.exception.custom.ApplicationUserNotFoundException;
import com.lazarev.resourceserver.exception.custom.RoleNotFoundException;
import com.lazarev.resourceserver.service.abstracts.RoleService;
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
        controllers = RoleController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RoleControllerTest {

    private static final String resourceUrl = "http://localhost:8090/api/v1/roles";

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    public void init(){
        objectWriter = new ObjectMapper().writer();
    }

    @MockBean
    private RoleService roleService;

    @Test
    @DisplayName(
            "Given - " +
            "When - getAllRoles request is performed " +
            "Then - List<Role>"
    )
    public void canGetRolesTest() throws Exception {
        //given
        List<Role> roles = List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER"));

        //when
        when(roleService.getAllRoles())
                .thenReturn(roles);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(resourceUrl))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = mvcResult.getResponse().getContentAsString();
        then(content).isEqualTo(objectWriter.writeValueAsString(roles));
    }

    @Test
    @DisplayName(
            "Given - RoleDto " +
            "When - saveNewRole request is performed " +
            "Then - result is OK"
    )
    public void canSaveNewRoleTest() throws Exception {
        //given
        RoleDto dto = new RoleDto(1L, "ROLE_NEW");

        willDoNothing()
                .given(roleService)
                .saveRole(dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                .post(resourceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName(
            "Given - RoleDto " +
            "When - updateRole request is performed " +
            "Then - result is OK"
    )
    public void canUpdateRoleTest_success() throws Exception {
        //given
        RoleDto dto = new RoleDto(1L, "ROLE_NEW");

        willDoNothing()
                .given(roleService)
                .updateRole(dto.getId(), dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                .put(resourceUrl + "/" + dto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName(
            "Given - RoleDto " +
            "When - updateRole request is performed " +
            "Then - throws RoleNotFoundException & notFound"
    )
    public void canUpdateRoleTest_fails() throws Exception {
        //given
        RoleDto dto = new RoleDto(1L, "ROLE_NEW");

        willThrow(RoleNotFoundException.class)
                .given(roleService)
                .updateRole(eq(dto.getId()), any());

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                .put(resourceUrl + "/" + dto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - deleteRole request is performed " +
            "Then - result is OK"
    )
    public void canDeleteRoleTest_success() throws Exception {
        //given
        Long id = 1L;

        //when
        when(roleService.deleteRoleById(id))
                .thenReturn(1);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                .delete(resourceUrl + "/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - deleteRole request is performed " +
            "Then - throws RoleNotFoundException & notFound"
    )
    public void canDeleteRoleTest_fail() throws Exception {
        //given
        Long id = 1L;

        //when
        when(roleService.deleteRoleById(id))
                .thenThrow(ApplicationUserNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                .delete(resourceUrl + "/" + id))
                .andExpect(status().isNotFound());
    }
}