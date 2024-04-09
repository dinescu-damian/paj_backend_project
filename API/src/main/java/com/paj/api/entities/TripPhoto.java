package com.paj.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "trip_photo")
public class TripPhoto {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long trip_photo_id;

    // Foreign key to Trip entity
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    // URL
    @Column(nullable = false)
    private String url;


    public TripPhoto() {
    }

    public void setTrip_photo_id(Long tripPhotoId) {
        this.trip_photo_id = tripPhotoId;
    }

    public Long getTrip_photo_id() {
        return trip_photo_id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
