package com.paj.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    // Role Id - primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;

    // Role name
    @Column(nullable = false, unique = true)
    private String role_name;

    public Role() {
    }

    public void setRole_id(Long roleId) {
        this.role_id = roleId;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_name(String roleName) {
        this.role_name = roleName;
    }

    public String getRole_name() {
        return role_name;
    }
}
