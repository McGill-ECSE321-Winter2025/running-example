package com.example.ecse_321_demo_backend.dao;

import com.example.ecse_321_demo_backend.models.Event;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, UUID> {
    Optional<Event> findById(UUID id);
}
