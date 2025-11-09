package com.userapplication.dto.kafka;

import java.time.Instant;

public record UserCreatedEvent (Long id, Instant createdAt, String email){

    public UserCreatedEvent(Long id, Instant createdAt, String email) {
        this.id = id;
        this.createdAt = createdAt;
        this.email = email;
    }
}
