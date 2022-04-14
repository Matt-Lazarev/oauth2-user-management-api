package com.lazarev.userclient.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Profile("production")
@AllArgsConstructor
@Configuration
public class DelegateOidcUserService {

    private final WebClient webClient;

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            ClientRegistration clientRegistration = userRequest.getClientRegistration();
            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            String principalName = oidcUser.getName();

            OAuth2AuthorizedClient client =
                    new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken);

            ResponseEntity<String[]> body = ResponseEntity
                            .status(HttpStatus.OK)
                            .body(webClient
                                    .get()
                                    .uri("http://localhost:8090/api/v1/users/info/" + principalName)
                                    .attributes(oauth2AuthorizedClient(client))
                                    .retrieve()
                                    .bodyToMono(String[].class)
                                    .block()
                            );

            Set<String> oidcUserAuthorities = new HashSet<>(accessToken.getScopes());
            if(body.getBody() != null){
                oidcUserAuthorities.addAll(List.of(body.getBody()));
            }

            Set<GrantedAuthority> mappedAuthorities = oidcUserAuthorities
                    .stream()
                    .map(authority -> authority.contains("SCOPE_")
                                    ? new SimpleGrantedAuthority(authority)
                                    : new SimpleGrantedAuthority("SCOPE_" + authority))
                    .collect(Collectors.toSet());
            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

            return oidcUser;
        };
    }
}
