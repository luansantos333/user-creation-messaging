package com.userapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "tb_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, length = 30, nullable = false)
    private String username;
    @Column(length = 100, nullable = false)
    private String password;
    @ManyToMany
    @JoinTable(name = "tb_user_role", joinColumns = @JoinColumn (name = "user_id"), inverseJoinColumns = @JoinColumn (name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();



}





