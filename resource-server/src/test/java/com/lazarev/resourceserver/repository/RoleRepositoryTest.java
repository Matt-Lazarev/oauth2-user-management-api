package com.lazarev.resourceserver.repository;

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
class RoleRepositoryTest {

    @Autowired
    private RoleRepository underTest;

    @BeforeEach
    public void init() {
        List<Role> roles = List.of(
                new Role(1L, "ROLE_ADMIN"),
                new Role(2L, "ROLE_USER"));


        underTest.saveAll(roles);
    }

    @Test
    @DisplayName(
            "Given - init state " +
            "When - findAll is called " +
            "Then - get List<Role>"
    )
    public void canFindAllTest(){
        //given

        //when
        List<Role> resultRoles = underTest.findAll();

        //then
        then(resultRoles.size()).isEqualTo(2);
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - findById is called " +
            "Then - get not empty Optional<Role>"
    )
    public void canFindByIdTest_success(){
        //given
        Long id = 1L;

        //when
        Optional<Role> role = underTest.findById(id);

        //then
        then(role).isNotEmpty();
        then(role.get().getId()).isNotNull();
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - findById is called " +
            "Then - get empty Optional<Role>"
    )
    public void canFindByIdTest_fail(){
        //given
        Long id = 3L;

        //when
        Optional<Role> role = underTest.findById(id);

        //then
        then(role).isEmpty();
    }

    @Test
    @DisplayName(
            "Given - Role name " +
            "When - findByName is called " +
            "Then - get not empty Optional<Role>"
    )
    public void canFindByNameTest_success(){
        //given
        String roleName = "ROLE_ADMIN";

        //when
        Optional<Role> role = underTest.findByName(roleName);

        //then
        then(role).isNotEmpty();
        then(role.get().getId()).isNotNull();
    }

    @Test
    @DisplayName(
            "Given - Role name " +
            "When - findByName is called " +
            "Then - get empty Optional<Role>"
    )
    public void canFindByNameTest_fail(){
        //given
        String roleName = "ROLE_NEW";

        //when
        Optional<Role> role = underTest.findByName(roleName);

        //then
        then(role).isEmpty();
    }

    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - deleteRoleById is called " +
            "Then - the only delete"
    )
    public void canDeleteRoleByIdTest_success(){
        //given
        Long id = 1L;

        //when
        Integer result = underTest.deleteRoleById(id);

        //then
        then(result).isEqualTo(1);
    }


    @Test
    @DisplayName(
            "Given - Role ID " +
            "When - deleteRoleById is called " +
            "Then - no one Role deleted"
    )
    public void canDeleteRoleByIdTest_fail(){
        //given
        Long id = 3L;

        //when
        Integer result = underTest.deleteRoleById(id);

        //then
        then(result).isEqualTo(0);
    }

}