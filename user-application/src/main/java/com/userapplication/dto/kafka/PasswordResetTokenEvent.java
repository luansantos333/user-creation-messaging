package com.userapplication.dto.kafka;

import java.time.Instant;

public record PasswordResetTokenEvent (String token, Instant  expirationTime, String username) {

    public PasswordResetTokenEvent(String token, Instant expirationTime, String username) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.username = username;
    }
}
