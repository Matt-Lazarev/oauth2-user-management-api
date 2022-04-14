package com.lazarev.userclient.controller.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lazarev.userclient.model.ApplicationUser;
import com.lazarev.userclient.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc
class ApplicationUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebClient webClientMock;

    @MockBean
    private WebClient.RequestHeadersSpec requestHeadersMock;

    @MockBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;

    @MockBean
    private WebClient.ResponseSpec responseMock;

    private ObjectWriter objectWriter;

    @BeforeEach
    public void init(){
        objectWriter = new ObjectMapper().writer();
    }

    @Test
    @DisplayName(
            "Given - List<ApplicationUser>" +
            "When - getAllUsers is called" +
            "Then - the same List<ApplicationUser>"
    )
    public void getAllUsersTest() throws Exception {
        String resourceUrl = "http://localhost:8090/api/v1/users";
        String getUrl =  "http://localhost:8080/api/v1/users";

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

        given(webClientMock.get())
                .willReturn(requestHeadersUriMock);

        given(requestHeadersUriMock.uri(resourceUrl))
                .willReturn(requestHeadersMock);

        given(requestHeadersMock.retrieve())
                .willReturn(responseMock);

        given(responseMock.bodyToMono(new ParameterizedTypeReference<List<ApplicationUser>>() {}))
                .willReturn(Mono.just(users));


        //When & Then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(getUrl))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content).isEqualTo(objectWriter.writeValueAsString(users));
    }
}