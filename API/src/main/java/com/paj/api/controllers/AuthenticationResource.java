package com.paj.api.controllers;

import jakarta.ws.rs.*;

@Path("/auth")
public class AuthenticationResource {
    @POST
    @Path("/login")
    @Produces("text/plain")
    public String login() {
        return "Successful login!";
    }
}