package com.userapplication.controller;

import com.userapplication.service.RegisteredClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final RegisteredClientService registeredClientService;


    public ClientController(RegisteredClientService registeredClientService) {
        this.registeredClientService = registeredClientService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> addNewClient(@RequestBody RegisteredClient registeredClient) {

        registeredClientService.save(registeredClient);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }




}
