package com.lazarev.resourceserver.service;

import com.lazarev.resourceserver.dto.ApplicationUserDto;
import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.entity.Role;
import com.lazarev.resourceserver.exception.custom.ApplicationUserNotFoundException;
import com.lazarev.resourceserver.repository.ApplicationUserRepository;
import com.lazarev.resourceserver.repository.RoleRepository;
import com.lazarev.resourceserver.service.abstracts.ApplicationUserService;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class ApplicationUserServiceTest {

    @Autowired
    private ApplicationUserService underTest;

    @MockBean
    private ApplicationUserRepository applicationUserRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Test
    @DisplayName(
            "Given - " +
            "When - getAllApplicationUsers is called " +
            "Then - get List<ApplicationUser>"
    )
    public void catGetAllApplicationUsersTest(){
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

        given(applicationUserRepository.findAll())
                .willReturn(users);

        //when
        List<ApplicationUser> resultUsers = underTest.getAllApplicationUsers();

        //then
        BDDAssertions.then(resultUsers.size()).isEqualTo(2);
        BDDAssertions.then(resultUsers).isEqualTo(users);
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - getApplicationUserById is called " +
            "Then - get ApplicationUser"
    )
    public void canGetApplicationUserByIdTest_success(){
        //given
        ApplicationUser applicationUser = new ApplicationUser(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER")));

        given(applicationUserRepository.findById(applicationUser.getId()))
                .willReturn(Optional.of(applicationUser));

        //when
        ApplicationUser resultApplicationUser = underTest.getApplicationUserById(applicationUser.getId());


        //then
        BDDAssertions.then(resultApplicationUser).isEqualTo(applicationUser);
        then(applicationUserRepository).should(only()).findById(any());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - getApplicationUserById is called " +
            "Then - throws ApplicationUserNotFoundException"
    )
    public void canGetApplicationUserByIdTest_fail(){
        //given
        ApplicationUser applicationUser = new ApplicationUser(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER")));

        given(applicationUserRepository.findById(applicationUser.getId()))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(()->underTest.getApplicationUserById(applicationUser.getId()))
                .isInstanceOf(ApplicationUserNotFoundException.class)
                .hasMessage(String.format("User with id = '%d' not found", applicationUser.getId()));
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser email " +
            "When - getUserRoles is called " +
            "Then - get String[] roles"
    )
    public void canGetUserRolesTest_success(){
        //given
        ApplicationUser applicationUser = new ApplicationUser(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER")));

        String[] roles = applicationUser.getRoles().stream().map(Role::getName).toArray(String[]::new);
        String email = applicationUser.getEmail();

        given(applicationUserRepository.findByEmail(email))
                .willReturn(Optional.of(applicationUser));

        //when
        String[] userRoles = underTest.getUserRoles(email);

        BDDAssertions.then(userRoles).isEqualTo(roles);
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser email " +
            "When - getUserRoles is called " +
            "Then - throws ApplicationUserNotFoundException"
    )
    public void canGetUserRolesTest_fail(){
        //given
        String email = "m@mail.ru";

        given(applicationUserRepository.findByEmail(email))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(()->underTest.getUserRoles(email))
                .isInstanceOf(ApplicationUserNotFoundException.class)
                .hasMessage(String.format("User with email = '%s' not found", email));
    }

    @Test
    @DisplayName(
            "Given - ApplicationUserDto " +
            "When - saveUser is called " +
            "Then - successful save"
    )
    public void canSaveUserTest(){
        //given
        ApplicationUserDto dto = new ApplicationUserDto(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of("ROLE_ADMIN", "ROLE_USER"));

        given(applicationUserRepository.save(any(ApplicationUser.class)))
                .willReturn(any(ApplicationUser.class));

        //when
        underTest.saveUser(dto);

        //then

        then(applicationUserRepository).should(only()).save(any());
        then(roleRepository).should(times(dto.getRoles().size())).findByName(anyString());
    }


    @Test
    @DisplayName(
            "Given - ApplicationUserDto and ApplicationUser ID " +
            "When - updateUser is called " +
            "Then - successful save"
    )
    public void canUpdateUserTest_success(){
        //given
        ApplicationUserDto dto = new ApplicationUserDto(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of("ROLE_ADMIN", "ROLE_USER"));

        ApplicationUser user = new ApplicationUser(dto.getId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRoles()
                        .stream()
                        .map(role -> new Role(1L, role))
                        .collect(Collectors.toList()));

        given(applicationUserRepository.save(any(ApplicationUser.class)))
                .willReturn(user);

        given(applicationUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        //when
        underTest.updateUser(dto, dto.getId());

        //then
        then(roleRepository).should(times(dto.getRoles().size())).findByName(anyString());
        then(applicationUserRepository).should(times(1)).save(any());
        then(applicationUserRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName(
            "Given - ApplicationUserDto and ApplicationUser ID " +
            "When - updateUser is called " +
            "Then - throws ApplicationUserNotFoundException"
    )
    public void canUpdateUserTest_fail(){
        //given
        ApplicationUserDto dto = new ApplicationUserDto(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of("ROLE_ADMIN", "ROLE_USER"));
        Long id = dto.getId();
        given(applicationUserRepository.findById(id))
                .willReturn(Optional.empty());

        //when & then
        then(applicationUserRepository).should(never()).save(any());
        assertThatThrownBy(()->underTest.updateUser(dto, id))
                .isInstanceOf(ApplicationUserNotFoundException.class)
                .hasMessage(String.format("User with id = '%d' not found", id));
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - deleteUser is called " +
            "Then - the only delete"
    )
    public void canDeleteUserTest_success(){
        //given
        Long id = 1L;

        given(applicationUserRepository.deleteApplicationUserById(id))
                .willReturn(1);

        //when
        Integer result = underTest.deleteUserById(id);

        //then
        BDDMockito.then(applicationUserRepository).should().deleteApplicationUserById(id);
        BDDAssertions.then(result).isEqualTo(1);
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - deleteUser is called " +
            "Then - no one ApplicationUser deleted"
    )
    public void canDeleteUserTest_fail(){
        //given
        Long id = 1L;

        given(applicationUserRepository.findById(id))
                .willReturn(Optional.empty());

        //when
        Integer result = underTest.deleteUserById(id);

        //then
        BDDMockito.then(applicationUserRepository).should().deleteApplicationUserById(id);
        BDDAssertions.then(result).isEqualTo(0);
    }
}