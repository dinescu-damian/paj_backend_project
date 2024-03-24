package com.paj.api.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendRequestId implements Serializable {
    private Long user_id_requester;
    private Long user_id_receiver;

    // getters, setters, equals, and hashCode methods
    public Long getUser_id_requester() {
        return user_id_requester;
    }

    public void setUser_id_requester(Long user_id_requester) {
        this.user_id_requester = user_id_requester;
    }

    public Long getUser_id_receiver() {
        return user_id_receiver;
    }

    public void setUser_id_receiver(Long user_id_receiver) {
        this.user_id_receiver = user_id_receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequestId that = (FriendRequestId) o;
        return Objects.equals(user_id_requester, that.user_id_requester) && Objects.equals(user_id_receiver, that.user_id_receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id_requester, user_id_receiver);
    }
}
