package com.paj.api.controllers;

import com.paj.api.DTOs.TripCreationDTO;
import com.paj.api.entities.Trip;
import com.paj.api.services.TripService;
import com.paj.api.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
    @Path("/getById/{id}")
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
    @Path("save")
    @RolesAllowed("USER")
    public void saveTrip(TripCreationDTO trip) {
        // Create a trip entity based in the data contained in the DTO
        var tripEntity = new Trip();
        var user = userService.findUserById(Long.valueOf(trip.userId()));

        tripEntity.setCity(trip.city());
        tripEntity.setCountry(trip.country());
        tripEntity.setDescription(trip.description());
        tripEntity.setSpending(trip.spending());
        tripEntity.setLikes(trip.likes());
        tripEntity.setRating(trip.rating());
        tripEntity.setUser(user);
        tripEntity.setDate(LocalDate.parse(trip.date(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());

        // Check that the trip is assigned to the authenticated user (otherwise users could create trips for other users(
        if(securityContext.getCallerPrincipal().getName().equals(user.getEmail()))
            tripService.saveTrip(tripEntity);
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
    @RolesAllowed("USER")
    @Path("/delete/{id}")
    public void deleteTrip(@PathParam("id") String id) {
        var trip = tripService.findTripById(Long.parseLong(id));
        var user = userService.findUserByEmail(securityContext.getCallerPrincipal().getName());

        if(Objects.equals(trip.getUser().getUser_id(), user.getUser_id()))
            tripService.deleteTrip(trip);
    }


}
