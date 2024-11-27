package com.example.ecse_321_demo_backend.dao;

import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.InPersonEvent;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.Location;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface EventRepository extends CrudRepository<Event, UUID> {
    @NonNull
    Optional<Event> findById(@NonNull UUID id);
    List<InPersonEvent> findInPersonEvents();
    List<OnlineEvent> findOnlineEvents();
    List<InPersonEvent> findInPersonEventsByLocation(Location location);
}
