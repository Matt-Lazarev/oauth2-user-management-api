package com.lazarev.resourceserver.repository;

import com.lazarev.resourceserver.entity.ApplicationUser;
import com.lazarev.resourceserver.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;

@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
class ApplicationUserRepositoryTest {

    @Autowired
    private ApplicationUserRepository underTest;

    @BeforeEach
    public void init(){

        List<ApplicationUser> users = List.of(
                new ApplicationUser(
                        1L,
                        "Matt",
                        "Lazarev",
                        "m@mail.ru",
                        "123",
                        List.of(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER"))),
                new ApplicationUser(
                        2L,
                        "Bob",
                        "Tyson",
                        "b@mail.ru",
                        "123",
                        List.of(new Role(2L, "ROLE_USER")))
        );
        underTest.saveAll(users);
    }

    @Test
    @DisplayName(
            "Given - init state " +
            "When - findAll is called " +
            "Then - get List<ApplicationUser>"
    )
    public void canFindAllTest(){
        //given

        //when
        List<ApplicationUser> resultUsers = underTest.findAll();

        //then
        then(resultUsers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - findById is called " +
            "Then - get not empty Optional<ApplicationUser>"
    )
    public void canFindByIdTest_success(){
        //given
        Long id = 1L;

        //when
        Optional<ApplicationUser> user = underTest.findById(id);

        //then
        then(user).isNotEmpty();
        then(user.get().getId()).isNotNull();
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - findById is called " +
            "Then - get empty Optional<ApplicationUser>"
    )
    public void canFindByIdTest_fail(){
        //given
        Long id = 3L;

        //when
        Optional<ApplicationUser> user = underTest.findById(id);

        //then
        then(user).isEmpty();
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser Email " +
            "When - findByEmail is called " +
            "Then - get not empty Optional<ApplicationUser>"
    )
    public void canFindByEmailTest_success(){
        //given
        String email = "m@mail.ru";

        //when
        Optional<ApplicationUser> user = underTest.findByEmail(email);

        //then
        then(user).isNotEmpty();
        then(user.get().getId()).isNotNull();
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser Email " +
            "When - findByEmail is called " +
            "Then - get empty Optional<ApplicationUser>"
    )
    public void canFindByEmailTest_fail(){
        //given
        String email = "empty@mail.ru";

        //when
        Optional<ApplicationUser> user = underTest.findByEmail(email);

        //then
        then(user).isEmpty();
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - deleteApplicationUserById is called " +
            "Then - the only delete"
    )
    public void canDeleteApplicationUserByIdTest_success(){
        //given
        Long id = 1L;

        //when
        Integer result = underTest.deleteApplicationUserById(id);

        //then
        then(result).isEqualTo(1);
    }

    @Test
    @DisplayName(
            "Given - ApplicationUser ID " +
            "When - deleteApplicationUserById is called " +
            "Then - no one ApplicationUser deleted"
    )
    public void canDeleteApplicationUserByIdTest_fail(){
        //given
        Long id = 3L;

        //when
        Integer result = underTest.deleteApplicationUserById(id);

        //then
        then(result).isEqualTo(0);
    }
}