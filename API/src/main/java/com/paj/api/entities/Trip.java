package com.paj.api.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
public class Trip {
    @Id
    @GeneratedValue
    private Long trip_id;

    // Foreign key to User entity
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // City
    @Column(nullable = false)
    private String city;

    // Country
    @Column(nullable = false)
    private String country;

    // Date
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime date;

    // Spending
    @Column(nullable = false)
    private Integer spending;

    // Rating
    @Column(nullable = false)
    private Integer rating;

    // Description
    @Column(nullable = false)
    private String description;

    // Likes
    @Column(nullable = false)
    private Integer likes;

    public Trip() {
    }

    public void setTrip_id(Long tripId) {
        this.trip_id = tripId;
    }

    public Long getTrip_id() {
        return trip_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getSpending() {
        return spending;
    }

    public void setSpending(Integer spending) {
        this.spending = spending;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
