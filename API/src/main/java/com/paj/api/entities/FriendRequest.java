package com.paj.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class FriendRequest {
    //Composite primary key between two user ids
    @EmbeddedId
    private FriendRequestId id;

    @Column(nullable = false)
    private String status;

    public FriendRequest() {
    }

    // getters and setters
    public FriendRequestId getId() {
        return id;
    }

    public void setId(FriendRequestId id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
