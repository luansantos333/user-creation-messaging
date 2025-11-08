package com.userapplication.dto;

import java.util.Set;

public record UserSecureDTO (Long id, String username, Set<RoleDTO> roles) {
    public UserSecureDTO(Long id, String username, Set<RoleDTO> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
