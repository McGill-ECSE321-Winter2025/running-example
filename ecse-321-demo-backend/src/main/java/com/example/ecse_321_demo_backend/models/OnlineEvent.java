package com.example.ecse_321_demo_backend.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ONLINE")
@Getter
@Setter
public class OnlineEvent extends Event {

    String inviteLink;

    public OnlineEvent() {
        super();
    }

    public OnlineEvent(
        UserAccount creator,
        String description,
        Timestamp start_time,
        Timestamp end_time,
        String inviteLink,
        Integer capacity
    ) {
        super(creator, description, start_time, end_time, capacity);
        this.inviteLink = inviteLink;
    }
}
