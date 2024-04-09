package com.paj.api.controllers;

import com.paj.api.entities.Role;
import com.paj.api.services.RoleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/roles")
public class RoleResource {
    @Inject
    private RoleService roleService;

    // Get role by id. Example: /roles/1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Role getRoleById(@PathParam("id") String id) {
        return roleService.findRoleById(Long.parseLong(id));
    }

    // Get all roles
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // Save role
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveRole(Role role) {
        roleService.saveRole(role);
    }

    // Update role
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(Role role) {
        roleService.updateRole(role);
    }

    // Delete role
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(Role role) {
        roleService.deleteRole(role);
    }
}
