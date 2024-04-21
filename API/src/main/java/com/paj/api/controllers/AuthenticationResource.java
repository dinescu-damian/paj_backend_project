package com.paj.api.controllers;

import com.paj.api.DTOs.UserDTO;
import com.paj.api.services.UserService;
import jakarta.annotation.security.RolesAllowed;
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

    @Inject
    UserService userService;


    /*
    *   On succesful login, return the user data in a Json
    */
    @POST
    @Path("/login")
    @Produces("application/json")
    @RolesAllowed("USER")
    public String login() {
        var user = userService.findUserByEmail(securityContext.getCallerPrincipal().getName());
        var userData = new UserDTO(user.getUser_id().toString(), user.getEmail());

        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}