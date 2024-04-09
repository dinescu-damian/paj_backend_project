package com.paj.api;

import com.paj.api.entities.Role;
import com.paj.api.entities.User;
import com.paj.api.services.RoleService;
import com.paj.api.services.UserService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

@ApplicationPath("/api")
public class HelloApplication extends Application {

}