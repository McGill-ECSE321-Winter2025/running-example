package com.example.ecse_321_demo_backend.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
@DiscriminatorValue("INPERSON")
public class InPersonEvent extends Event {

    @ManyToOne
    private Location location;

    public InPersonEvent() {
        super();
    }

    public InPersonEvent(
        UserAccount creator,
        String description,
        Timestamp start_time,
        Timestamp end_time,
        Location location
    ) {
        super(creator, description, start_time, end_time);
        this.location = location;
    }
}
