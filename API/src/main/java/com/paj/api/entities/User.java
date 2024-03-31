package com.paj.api.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class User {
    // User ID - primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    // Email
    @Column(nullable = false, unique = true)
    private String email;

    // Password (as a hash)
    @Column(nullable = false)
    private String password;

    // Name
    @Column(nullable = false)
    private String name;

    // Age
    @Column()
    private Integer age;

    // Location - optional
    @Column()
    private String location;

    // Link to Role entity - many to many
    @Column(nullable = false)
    @ManyToMany
    Set<Role> roles;

    public User() {
    }

    public void setUser_id(Long userId) {
        this.user_id = userId;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
