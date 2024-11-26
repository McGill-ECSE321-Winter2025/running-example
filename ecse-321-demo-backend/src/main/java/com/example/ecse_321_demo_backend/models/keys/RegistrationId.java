package com.example.ecse_321_demo_backend.models.keys;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RegistrationId implements Serializable {

  private UUID eventId;
  private UUID userId;

  // Default constructor
  public RegistrationId() {}

  // Equals and HashCode methods
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegistrationId that = (RegistrationId) o;
    return (
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(userId, that.userId)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId, userId);
  }
}
