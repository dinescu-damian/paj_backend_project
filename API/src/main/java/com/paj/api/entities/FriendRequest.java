package com.paj.api.entities;

import jakarta.persistence.*;

@Entity
public class FriendRequest {
    //Composite primary key between two user ids
    @EmbeddedId
    private FriendRequestId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id_requester")
    private User user_requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id_receiver")
    private User user_receiver;

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
