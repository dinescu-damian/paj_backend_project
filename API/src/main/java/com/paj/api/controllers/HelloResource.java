package com.paj.api.controllers;

import com.paj.api.entities.Role;
import com.paj.api.entities.User;
import com.paj.api.services.RoleService;
import com.paj.api.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;


@Path("/hello-world")
public class HelloResource {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @GET
    @Produces("text/plain")
    public String hello() {

        User user = new User();
        //Role role = new Role();

        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setName("Test User");
        user.setAge(23);
        user.setLocation("Test Location");

        //role.setRole_name("User");

        //user.setRoles(Set.of(role));
        //role.setUsers(Set.of(user));

        userService.saveUser(user);
        //roleService.saveRole(role);

        return "Hello, World!";
    }
}