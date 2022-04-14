package com.lazarev.oauthserver.service;

import com.lazarev.oauthserver.entity.ApplicationUser;
import com.lazarev.oauthserver.entity.Role;
import com.lazarev.oauthserver.repository.ApplicationUserRepository;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class ApplicationUserDetailsServiceTest {

    @Autowired
    private ApplicationUserDetailsService underTest;

    @MockBean
    private ApplicationUserRepository applicationUserRepository;

    @Test
    @DisplayName(
            "Given - ApplicationUser Email " +
            "When - loadUserByUsername is called " +
            "Then - get UserDetails is equivalent to ApplicationUser"
    )
    public void canGetApplicationUserByEmailTest_success(){
        //given
        ApplicationUser applicationUser = new ApplicationUser(
                1L,
                "Matt",
                "Lazarev",
                "m@mail.ru",
                "123",
                List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER")));

        given(applicationUserRepository.findByEmail(applicationUser.getEmail()))
                .willReturn(Optional.of(applicationUser));

        //when
        UserDetails resultUser = underTest
                .loadUserByUsername(applicationUser.getEmail());


        //then
        BDDAssertions.then(resultUser.getAuthorities().size()).isEqualTo(applicationUser.getRoles().size());
        then(applicationUserRepository).should(only()).findByEmail(any());
    }

    @Test
    @DisplayName(
            "Given - List<Role> " +
            "When - getAuthorities is called " +
            "Then - get List<GrantedAuthorities> is equivalent to List<Role> "
    )
    public void canGetAuthoritiesTest(){
        //given
        List<Role> roles = List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER"));

        //when

        List<? extends GrantedAuthority> result =
                ReflectionTestUtils.invokeMethod(underTest, "getAuthorities", roles);

        //then
        BDDAssertions.then(result.size()).isEqualTo(roles.size());
    }
}