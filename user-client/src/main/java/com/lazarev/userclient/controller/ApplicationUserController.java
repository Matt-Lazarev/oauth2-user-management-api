package com.lazarev.userclient.controller;

import com.lazarev.userclient.model.ApplicationUser;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.annotation.Nullable;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class ApplicationUserController {

    //Using WebClient due to OAuthRestTemplate is deprecated
    private final WebClient webClient;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationUser>> getAllUsers(
            @Nullable @RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
                    OAuth2AuthorizedClient client){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(webClient
                        .get()
                        .uri("http://localhost:8090/api/v1/users")
                        .attributes(oauth2AuthorizedClient(client))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ApplicationUser>>() {})
                        .block()
                );
    }
}
