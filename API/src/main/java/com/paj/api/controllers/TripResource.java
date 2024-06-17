package com.paj.api.controllers;

import com.paj.api.DTOs.TripDTO;
import com.paj.api.entities.Trip;
import com.paj.api.services.TripService;
import com.paj.api.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public List<TripDTO> getAllTripsForUser() {
        var user = userService.findUserByEmail(securityContext.getCallerPrincipal().getName());
        var tripEntities = tripService.getAllTripsByUserId(user.getUser_id());

        List<TripDTO> res = new ArrayList<>();
        tripEntities.forEach(trip -> res.add(new TripDTO(
                trip.getTrip_id(),
                user.getUser_id().toString(),
                trip.getCity(),
                trip.getCountry(),
                trip.getDate().toString(),
                trip.getSpending(),
                trip.getRating(),
                trip.getLikes(),
                trip.getDescription()))
        );

        return res;
    }

    // Save trip
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("save")
    @RolesAllowed("USER")
    public TripDTO saveTrip(TripDTO trip) {
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
            if(trip.tripId() == null) {
                tripService.saveTrip(tripEntity);

                // Return the created trip, provide the new ID as well
                return new TripDTO(
                        tripEntity.getTrip_id(),
                        user.getUser_id().toString(),
                        tripEntity.getCity(),
                        tripEntity.getCountry(),
                        tripEntity.getDate().toString(),
                        tripEntity.getSpending(),
                        tripEntity.getRating(),
                        tripEntity.getLikes(),
                        tripEntity.getDescription()
                        );
            }
            else {
                tripEntity.setTrip_id(trip.tripId());
                tripService.updateTrip(tripEntity);
                return trip;
            }

        return null;
    }

    // Delete trip
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("USER")
    @Path("/delete/{id}")
    public Response deleteTrip(@PathParam("id") String id) {
        var trip = tripService.findTripById(Long.parseLong(id));

        if(trip == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        var user = userService.findUserByEmail(securityContext.getCallerPrincipal().getName());

        if(Objects.equals(trip.getUser().getUser_id(), user.getUser_id())) {
            tripService.deleteTrip(trip);
            return Response.status(Response.Status.OK).build();
        }
        else
            return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
