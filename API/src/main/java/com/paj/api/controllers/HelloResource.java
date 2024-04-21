package com.paj.api.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/hello-world")
public class HelloResource {

    @GET
    @Produces("text/plain")
    @RolesAllowed("USER")
    public String hello() {
        return "Hello, World!";
    }
}