package com.userapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "tb_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class RoleEntity implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String roleDescription;
    private String roleName;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<UserEntity> users = new ArrayList<>();

    public RoleEntity(Long roleId, String roleDescription) {

        this.roleId = roleId;
        this.roleDescription = roleDescription;

    }


    @Override
    public String getAuthority() {
        return roleName;
    }
}
