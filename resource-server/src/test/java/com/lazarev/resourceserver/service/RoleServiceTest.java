package com.lazarev.resourceserver.service;

import com.lazarev.resourceserver.dto.RoleDto;
import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.exception.custom.RoleNotFoundException;
import com.lazarev.resourceserver.repository.ApplicationUserRepository;
import com.lazarev.resourceserver.repository.RoleRepository;
import com.lazarev.resourceserver.service.abstracts.RoleService;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class RoleServiceTest {

    @Autowired
    private RoleService underTest;

    @MockBean
    private ApplicationUserRepository applicationUserRepository;

    @MockBean
    private RoleRepository roleRepository;


    @Test
    @DisplayName(
            "Given - " +
            "When - getAllRoles is called " +
            "Then - get List<Role>"
    )
    public void catGetAllRolesTest(){
        //given

        List<Role> userRoles = List.of(
                new Role(1L, "ROLE_ADMIN"),
                new Role(2L, "ROLE_USER"));


        given(roleRepository.findAll())
                .willReturn(userRoles);

        //when
        List<Role> resultRoles = underTest.getAllRoles();

        //then
        BDDAssertions.then(resultRoles.size()).isEqualTo(2);
        BDDAssertions.then(resultRoles).isEqualTo(userRoles);
    }

    @Test
    @DisplayName(
            "Given - RoleDto " +
            "When - saveRole is called " +
            "Then - successful save"
    )
    public void canSaveRoleTest(){
        //given
        RoleDto dto = new RoleDto(1L, "ROLE_ADMIN");

        given(applicationUserRepository.save(any(ApplicationUser.class)))
                .willReturn(any(ApplicationUser.class));

        //when
        underTest.saveRole(dto);

        //then
        then(roleRepository).should(only()).save(any());
    }

    @Test
    @DisplayName(
            "Given - RoleDto and Role ID " +
            "When - updateRole is called " +
            "Then - successful save"
    )
    public void canUpdateRoleTest_success(){
        //given
        RoleDto dto = new RoleDto(1L, "ROLE_USER");
        Role role = new Role(dto.getId(), dto.getName());

        given(roleRepository.save(any(Role.class)))
                .willReturn(role);

        given(roleRepository.findById(role.getId()))
                .willReturn(Optional.of(role));

        //when
        underTest.updateRole(dto.getId(), dto);

        //then
        then(roleRepository).should(times(1)).save(any());
        then(roleRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName(
            "Given - RoleDto and Role ID " +
            "When - updateRole is called " +
            "Then - throws RoleNotFoundException"
    )
    public void canUpdateRoleTest_fail(){
        //given
        RoleDto dto = new RoleDto(1L, "ROLE_USER");
        Role role = new Role(dto.getId(), dto.getName());

        given(roleRepository.findById(role.getId()))
                .willReturn(Optional.empty());


        //when & then

        assertThatThrownBy(()->underTest.updateRole(dto.getId(), dto))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage(String.format("Role with id = '%d' not found", dto.getId()));
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - deleteRole is called " +
            "Then - the only delete"
    )
    public void canDeleteRoleTest_success(){
        //given
        Long id = 1L;

        given(roleRepository.deleteRoleById(id))
                .willReturn(1);

        //when
        Integer result = underTest.deleteRoleById(id);

        //then
        BDDMockito.then(roleRepository).should().deleteRoleById(id);
        BDDAssertions.then(result).isEqualTo(1);
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - deleteRole is called " +
            "Then - no one Role deleted"
    )
    public void canDeleteRoleTest_fail(){
        //given
        Long id = 1L;

        given(roleRepository.findById(id))
                .willReturn(Optional.empty());

        //when
        Integer result = underTest.deleteRoleById(id);

        //then
        BDDMockito.then(roleRepository).should().deleteRoleById(id);
        BDDAssertions.then(result).isEqualTo(0);
    }

}