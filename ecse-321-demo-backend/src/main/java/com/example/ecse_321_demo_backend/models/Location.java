package com.example.ecse_321_demo_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  private User created_by;

  private Timestamp created_at;

  private String description;

  private Integer capacity;

  public Location(User creator, String description, Integer capacity) {
    this.created_by = creator;
    this.created_at = Timestamp.from(Instant.now());
    this.description = description;
    this.capacity = capacity;
  }
}
