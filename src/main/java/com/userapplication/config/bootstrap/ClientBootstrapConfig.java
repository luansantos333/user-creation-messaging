package com.userapplication.config.bootstrap;

import com.userapplication.service.RegisteredClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class ClientBootstrapConfig {


    @Value("${client.name}")
    private String clientName;
    @Value("${client.secret}")
    private String clientSecret;
    @Value("${client.id}")
    private String clientId;
    @Value("${client.tokenTTLInSeconds}")
    private Long tokenDurationInSeconds;
    @Value("${client.redirect-uri}")
    private String redirectUri;


    @Bean
    CommandLineRunner boostrapClient(RegisteredClientService registeredClientService, PasswordEncoder passwordEncoder) {

        return args -> {


            if (registeredClientService.findByClientId(clientId) == null) {

                RegisteredClient adminClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientName(clientName)
                        .clientSecret(clientSecret)
                        .clientId(clientId)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .scope(OidcScopes.OPENID)
                        .redirectUri(redirectUri)
                        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofSeconds(tokenDurationInSeconds)).build())
                        .build();

                registeredClientService.save(adminClient);

            }
        };

    }



}
