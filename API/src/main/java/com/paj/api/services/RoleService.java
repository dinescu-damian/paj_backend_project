package com.paj.api.services;

import com.paj.api.entities.Role;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class RoleService {
    @PersistenceContext
    private EntityManager em;

    // Find role by id
    public Role findRoleById(Long id) {
        return em.find(Role.class, id);
    }

    // Save role
    public void saveRole(Role role) {
        em.persist(role);
    }

    // Update role
    public void updateRole(Role role) {
        em.merge(role);
    }

    // Delete role
    public void deleteRole(Role role) {
        em.remove(role);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }


}
