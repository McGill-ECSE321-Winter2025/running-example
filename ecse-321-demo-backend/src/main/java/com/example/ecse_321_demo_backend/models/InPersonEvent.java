package com.example.ecse_321_demo_backend.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.sql.Timestamp;

@Entity
@DiscriminatorValue("INPERSON")
public class InPersonEvent extends Event {

    private String location;

    public InPersonEvent() {
        super();
    }

    public InPersonEvent(
        UserAccount creator,
        String description,
        Timestamp start_time,
        Timestamp end_time,
        String location
    ) {
        super(creator, description, start_time, end_time);
        this.location = location;
    }
}
