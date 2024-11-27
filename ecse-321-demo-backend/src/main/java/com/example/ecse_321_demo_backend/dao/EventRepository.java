package com.example.ecse_321_demo_backend.dao;

import com.example.ecse_321_demo_backend.models.Event;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface EventRepository extends CrudRepository<Event, UUID> {
  @NonNull
  Optional<Event> findById(@NonNull UUID id);
}
