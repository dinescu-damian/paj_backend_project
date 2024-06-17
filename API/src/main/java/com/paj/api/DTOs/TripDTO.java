package com.paj.api.DTOs;

import com.paj.api.entities.Trip;

public record TripDTO(Long tripId,
                      String userId,
                      String city,
                      String country,
                      String date,
                      int spending,
                      int rating,
                      int likes,
                      String description){};
