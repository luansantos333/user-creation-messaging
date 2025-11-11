package com.userapplication.dto;

public record UserResetTokenDTO(String username) {

    public UserResetTokenDTO(String username) {
        this.username = username;
    }
}
