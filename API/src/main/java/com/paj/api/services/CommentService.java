package com.paj.api.services;

import com.paj.api.entities.Comment;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class CommentService {
    @PersistenceContext
    private EntityManager em;

    // Find comment by id
    public Comment findCommentById(Long id) {
        return em.find(Comment.class, id);
    }

    // Save comment
    public void saveComment(Comment comment) {
        em.persist(comment);
    }

    // Update comment
    public void updateComment(Comment comment) {
        em.merge(comment);
    }

    // Delete comment
    public void deleteComment(Comment comment) {
        em.remove(comment);
    }

    // Get all comments
    public List<Comment> getAllComments() {
        return em.createQuery("SELECT c FROM Comment c", Comment.class).getResultList();
    }

    // Get all comments by trip id
    public List<Comment> getAllCommentsByTripId(Long tripId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.trip.trip_id = :tripId", Comment.class)
                .setParameter("tripId", tripId)
                .getResultList();
    }

    // Get all comments by user id
    public List<Comment> getAllCommentsByUserId(Long userId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.user.user_id = :userId", Comment.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
