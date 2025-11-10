package com.userapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table (name = "tb_reset_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassswordResetTokenEntity {

    public static final Integer expirationTimeInMinutes = 30;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne (targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn (nullable = false, name = "user_id")
    private UserEntity user;
    private Instant expirationTime;





}
