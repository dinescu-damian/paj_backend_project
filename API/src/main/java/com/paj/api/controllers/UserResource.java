package com.paj.api.controllers;

import com.paj.api.entities.User;
import com.paj.api.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/users")
public class UserResource {

    @Inject
    private UserService userService;

    // Get user by id. Example: /users/1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public User getUserById(@PathParam("id") String id) {
        return userService.findUserById(Long.parseLong(id));
    }

    // Get all users
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Save user
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveUser(User user) {
        userService.saveUser(user);
    }

    // Update user
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(User user) {
        userService.updateUser(user);
    }

    // Delete user
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteUser(User user) {
        userService.deleteUser(user);
    }
}
