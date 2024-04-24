package com.paj.api.controllers;

import com.paj.api.entities.Trip;
import com.paj.api.services.TripService;
import com.paj.api.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/trips")
public class TripResource {

    @Inject
    SecurityContext securityContext;

    @Inject
    private TripService tripService;

    @Inject
    private UserService userService;

    // Get trip by id. Example: /trips/1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Trip getTripById(@PathParam("id") String id) {
        return tripService.findTripById(Long.parseLong(id));
    }

    // Get all trips
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("my-trips")
    @RolesAllowed("USER")
    public List<Trip> getAllTripsForUser() {
        var user = userService.findUserByEmail(securityContext.getCallerPrincipal().getName());
        return tripService.getAllTripsByUserId(user.getUser_id());
    }

    // Save trip
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveTrip(Trip trip) {
        tripService.saveTrip(trip);
    }

    // Update trip
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateTrip(Trip trip) {
        tripService.updateTrip(trip);
    }

    // Delete trip
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteTrip(Trip trip) {
        tripService.deleteTrip(trip);
    }


}
