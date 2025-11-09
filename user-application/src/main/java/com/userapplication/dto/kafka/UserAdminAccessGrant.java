package com.userapplication.dto.kafka;

import java.time.Instant;

public record UserAdminAccessGrant (String email, String message, Instant createdAt){

    public UserAdminAccessGrant(String email, String message, Instant createdAt) {
        this.email = email;
        this.message = message;
        this.createdAt = createdAt;
    }
}
