package com.paj.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long comment_id;

    // Foreign key to Trip entity
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    // Foreign key to User entity
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Content
    @Column(nullable = false)
    private String content;

    public Comment() {
    }

    public void setComment_id(Long commentId) {
        this.comment_id = commentId;
    }

    public Long getComment_id() {
        return comment_id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
