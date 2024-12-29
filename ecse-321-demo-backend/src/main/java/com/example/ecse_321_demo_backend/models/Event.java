package com.example.ecse_321_demo_backend.models;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EVENT_TYPE")
@NoArgsConstructor
@Getter
@Setter
public abstract class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UserAccount createdBy;

    private Timestamp createdAt;

    private String description;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer participantsCount;

    private Integer capacity;

    public Event(
        UserAccount creator,
        String description,
        Timestamp start_time,
        Timestamp end_time,
        Integer capacity
    ) {
        this.createdBy = creator;
        this.createdAt = Timestamp.from(Instant.now());
        this.description = description;
        this.startTime = start_time;
        this.endTime = end_time;
        this.participantsCount = 0;
        this.capacity = capacity;
    }

    @Transient
    public String getEventType() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }
}
