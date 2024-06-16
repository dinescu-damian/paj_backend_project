package com.paj.api.services;

import com.paj.api.entities.Trip;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class TripService {

    @PersistenceContext
    private EntityManager em;

    // Find trip by id
    public Trip findTripById(Long id) {
        return em.find(Trip.class, id);
    }

    // Save trip
    public void saveTrip(Trip trip) {
        em.persist(trip);
    }

    // Update trip
    public void updateTrip(Trip trip) {
        em.merge(trip);
    }

    // Delete trip
    public void deleteTrip(Trip trip) {
        em.remove(em.merge(trip));
    }

    // Get all trips
    public List<Trip> getAllTrips() {
        return em.createQuery("SELECT t FROM Trip t", Trip.class).getResultList();
    }

    // Get all trips by user id
    public List<Trip> getAllTripsByUserId(Long userId) {
        return em.createQuery("SELECT t FROM Trip t WHERE t.user.user_id = :userId", Trip.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
