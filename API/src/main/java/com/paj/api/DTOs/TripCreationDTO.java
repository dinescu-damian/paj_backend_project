package com.paj.api.DTOs;

public record TripCreationDTO(String userId,
                              String city,
                              String country,
                              String date,
                              int spending,
                              int rating,
                              int likes,
                              String description){};
