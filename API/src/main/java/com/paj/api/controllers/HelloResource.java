package com.paj.api.controllers;

import com.paj.api.entities.User;
import com.paj.api.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/hello-world")
public class HelloResource {

    @Inject
    private UserService userService;

    @GET
    @Produces("text/plain")
    public String hello() {

        User user = userService.findUserById(1L);

        return "Hello, World!";
    }
}