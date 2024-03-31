package com.paj.api.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Role {
    // Role Id - primary key
    @Id
    @GeneratedValue
    private Long role_id;

    // Role name
    @Column(nullable = false, unique = true)
    private String role_name;

    // Link to User entity - many to many
    @Column()
    @ManyToMany
    Set<User> users;

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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
