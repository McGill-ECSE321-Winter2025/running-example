package com.example.ecse_321_demo_backend.models;

import com.example.ecse_321_demo_backend.models.keys.RegistrationId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class Registration {

    @EmbeddedId
    private RegistrationId id;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserAccount user;

    public Registration() {}

    public Registration(Event event, UserAccount user) {
        this.id = new RegistrationId();
        this.event = event;
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public UserAccount getUser() {
        return user;
    }
}
