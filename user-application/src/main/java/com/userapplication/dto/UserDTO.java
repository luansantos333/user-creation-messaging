package com.userapplication.dto;

import java.util.Set;

public record UserDTO (String username, String password, Set<RoleDTO> roles){


    public UserDTO(String username, String password, Set<RoleDTO> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}
