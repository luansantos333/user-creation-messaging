package com.userapplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userapplication.entity.ClientEntity;
import com.userapplication.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RegisteredClientService implements RegisteredClientRepository {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private  ObjectMapper objectMapper;




    @Override
    public void save(RegisteredClient registeredClient) {

        try {
            ClientEntity clientEntity = new ClientEntity();
            registeredClientToClient(registeredClient, clientEntity);
            clientRepository.save(clientEntity);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public RegisteredClient findById(String id) {

        try {

            ClientEntity clientEntity = clientRepository.findById(Long.parseLong(id)).orElseThrow(() -> new NoSuchElementException("No client found with the id"));
            RegisteredClient.Builder registeredClient = RegisteredClient.withId(clientEntity.getId().toString());
            registeredClient
                    .clientId(clientEntity.getClient_id())
                    .clientName(clientEntity.getClientName())
                    .clientSecret(clientEntity.getClient_secret());
            int authenticationMethodsLength = clientEntity.getClientAuthenticationMethods().split(",").length;

            for (int i = 0; i < authenticationMethodsLength; i++) {

                registeredClient.clientAuthenticationMethod(new ClientAuthenticationMethod(clientEntity.getClientAuthenticationMethods().split(",")[i]));
            }

            int scopesLength = clientEntity.getScopes().split(",").length;

            for (int i = 0; i < scopesLength; i++) {


                registeredClient.scope(clientEntity.getScopes().split(",")[i]);

            }

            int authorizationGrantTypesLength = clientEntity.getAuthorizationGrantTypes().split(",").length;

            for (int i = 0; i < authorizationGrantTypesLength; i++) {

                registeredClient.authorizationGrantType(new AuthorizationGrantType(clientEntity.getAuthorizationGrantTypes().split(",")[i]));


            }

            int redirectUrisLength = clientEntity.getRedirectUris().split(",").length;

            for (int i = 0; i < redirectUrisLength; i++) {

                registeredClient.redirectUri(clientEntity.getRedirectUris().split(",")[i]);

            }

            registeredClient.clientIdIssuedAt(clientEntity.getClient_created_at())
                    .clientSecretExpiresAt(clientEntity.getClient_secret_expires_at());

            Map<String, Object> settingsMap = objectMapper.readValue(
                    clientEntity.getClientSettings(),
                    Map.class
            );

            Map<String, Object> tokenSettings = objectMapper.readValue(

                    clientEntity.getTokenSettings(),
                    Map.class

            );

            registeredClient.clientSettings(ClientSettings.withSettings(settingsMap).build());
            registeredClient.tokenSettings(TokenSettings.withSettings(tokenSettings).build());

            return registeredClient.build();


        } catch (JsonProcessingException e) {


            e.getMessage();
            throw new RuntimeException(e);


        }



    }

    @Override
    public RegisteredClient findByClientId(String clientId) {


        try {
        ClientEntity clientEntity = clientRepository.findByClientId(clientId);
        RegisteredClient.Builder registeredClient = RegisteredClient.withId(clientEntity.getId().toString());
        registeredClient
                .clientId(clientEntity.getClient_id())
                .clientName(clientEntity.getClientName())
                .clientSecret(clientEntity.getClient_secret());
        int authenticationMethodsLength = clientEntity.getClientAuthenticationMethods().split(",").length;

        for (int i = 0; i < authenticationMethodsLength; i++) {

            registeredClient.clientAuthenticationMethod(new ClientAuthenticationMethod(clientEntity.getClientAuthenticationMethods().split(",")[i]));
        }

        int scopesLength = clientEntity.getScopes().split(",").length;

        for (int i = 0; i < scopesLength; i++) {


            registeredClient.scope(clientEntity.getScopes().split(",")[i]);

        }

        int authorizationGrantTypesLength = clientEntity.getAuthorizationGrantTypes().split(",").length;

        for (int i = 0; i < authorizationGrantTypesLength; i++) {

            registeredClient.authorizationGrantType(new AuthorizationGrantType(clientEntity.getAuthorizationGrantTypes().split(",")[i]));


        }

        int redirectUrisLength = clientEntity.getRedirectUris().split(",").length;

        for (int i = 0; i < redirectUrisLength; i++) {

            registeredClient.redirectUri(clientEntity.getRedirectUris().split(",")[i]);

        }

        registeredClient.clientIdIssuedAt(clientEntity.getClient_created_at())
                .clientSecretExpiresAt(clientEntity.getClient_secret_expires_at());

        Map<String, Object> settingsMap = objectMapper.readValue(
                clientEntity.getClientSettings(),
                Map.class
        );

        Map<String, Object> tokenSettings = objectMapper.readValue(

                clientEntity.getTokenSettings(),
                Map.class

        );

        registeredClient.clientSettings(ClientSettings.withSettings(settingsMap).build());
        registeredClient.tokenSettings(TokenSettings.withSettings(tokenSettings).build());

        return registeredClient.build();


        }

        catch (JsonProcessingException e) {

            e.getMessage();
            throw new RuntimeException(e);

        }

    }


    private void registeredClientToClient(RegisteredClient registeredClient, ClientEntity clientEntity) throws JsonProcessingException {

        clientEntity.setClient_id(registeredClient.getClientId());
        clientEntity.setClient_secret(registeredClient.getClientSecret());
        clientEntity.setClientName(registeredClient.getClientName());
        clientEntity.setScopes(String.join(",", registeredClient.getScopes()));
        clientEntity.setRedirectUris(String.join(",", registeredClient.getRedirectUris()));
        clientEntity.setClient_secret_expires_at(registeredClient.getClientSecretExpiresAt());
        clientEntity.setClientSettings(objectMapper.writeValueAsString(registeredClient.getClientSettings().getSettings()));
        clientEntity.setClientAuthenticationMethods(registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue).collect(Collectors.joining(",")));
        clientEntity.setAuthorizationGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue).collect(Collectors.joining(",")));

        clientEntity.setTokenSettings(objectMapper.writeValueAsString(registeredClient.getTokenSettings().getSettings()));

        clientEntity.setClient_created_at(Instant.now());

    }




}
