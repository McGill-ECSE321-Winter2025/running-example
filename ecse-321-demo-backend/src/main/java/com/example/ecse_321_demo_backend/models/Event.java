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

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EVENT_TYPE")
public abstract class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  private UserAccount created_by;

  private Timestamp created_at;

  private String description;

  private Timestamp start_time;

  private Timestamp end_time;

  public Event() {}

  public Event(
    UserAccount creator,
    String description,
    Timestamp start_time,
    Timestamp end_time
  ) {
    this.created_by = creator;
    this.created_at = Timestamp.from(Instant.now());
    this.description = description;
    this.start_time = start_time;
    this.end_time = end_time;
  }

  @Transient
  public String getEventType() {
    return this.getClass().getAnnotation(DiscriminatorValue.class).value();
  }
}
