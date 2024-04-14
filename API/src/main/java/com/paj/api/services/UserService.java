package com.paj.api.services;

import com.paj.api.entities.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    // Find user by id
    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    // Save user
    public void saveUser(User user) {
        em.persist(user);
    }

    // Update user
    public void updateUser(User user) {
        em.merge(user);
    }

    // Delete user
    public void deleteUser(User user) {
        em.remove(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User findUserByEmail(String email) {
        return em.createQuery("SELECT u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }
}
