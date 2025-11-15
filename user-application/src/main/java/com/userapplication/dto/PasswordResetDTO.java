package com.userapplication.dto;

public record PasswordResetDTO (String username, String token, String newPassword){


    public PasswordResetDTO(String username, String token, String newPassword) {
        this.username = username;
        this.token = token;
        this.newPassword = newPassword;
    }
}
