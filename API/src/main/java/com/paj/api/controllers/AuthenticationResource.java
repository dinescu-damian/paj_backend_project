package com.paj.api.controllers;

import com.paj.api.DTOs.RegisterCredentialsDTO;
import com.paj.api.DTOs.UserDTO;
import com.paj.api.entities.Role;
import com.paj.api.entities.User;
import com.paj.api.services.RoleService;
import com.paj.api.services.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.ws.rs.*;

import java.util.HashSet;

@Path("/auth")
public class AuthenticationResource {
    @Inject
    SecurityContext securityContext;

    @Inject
    UserService userService;

    @Inject
    RoleService roleService;

    @Inject
    Pbkdf2PasswordHash hasher;

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

    @POST
    @Path("/register")
    @Produces("application/json")
    @PermitAll
    public String register(RegisterCredentialsDTO credentialsDTO) {
        var newUser = new User();
        var userRole = roleService.getAllRoles().stream()
                .filter(role -> role.getRole_name().equals("USER"))
                .findFirst().get();
        var roleSet = new HashSet<Role>();
        roleSet.add(userRole);

        newUser.setAge(credentialsDTO.age());
        newUser.setEmail(credentialsDTO.email());
        newUser.setName(credentialsDTO.name());
        newUser.setLocation(credentialsDTO.location());
        newUser.setPassword(hasher.generate(credentialsDTO.password().toCharArray()));
        newUser.setRoles(roleSet);


        userService.saveUser(newUser);
        newUser = userService.findUserByEmail(credentialsDTO.email());
        var userData = new UserDTO(newUser.getUser_id().toString(), newUser.getEmail());

        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}