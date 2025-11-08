package com.userapplication.model;

import com.userapplication.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class User extends UserEntity implements UserDetails {

    public User() {
        super();
    }

    public User(UserEntity userEntity) {
        this.setId(userEntity.getId());
        this.setUsername(userEntity.getUsername());
        this.setPassword(userEntity.getPassword());
        this.setRoles(userEntity.getRoles());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
