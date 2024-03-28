package com.paj.api.controllers;

import com.paj.api.DTOs.UserDTO;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;

@Path("/auth")
public class AuthenticationResource {

    @Inject
    SecurityContext securityContext;

    /*
    *   On succesful login, return the user data in a Json
    */
    @POST
    @Path("/login")
    @Produces("application/json")
    public String login() {
        // TODO: id should come from DB
        UserDTO userData = new UserDTO("1", securityContext.getCallerPrincipal().getName());
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}