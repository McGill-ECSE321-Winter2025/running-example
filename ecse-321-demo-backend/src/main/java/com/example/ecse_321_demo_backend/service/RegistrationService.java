package com.example.ecse_321_demo_backend.service;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.dao.RegistrationRepository;
import com.example.ecse_321_demo_backend.exceptions.UnauthedException;
import com.example.ecse_321_demo_backend.middleware.UserContext;
import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.Registration;
import com.example.ecse_321_demo_backend.models.UserAccount;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserContext userContext;

    @Transactional
    public void registerForEvent(UUID eventId) {
        UserAccount user = userContext.getCurrentUser();
        Event event = eventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getStartTime().before(Timestamp.from(Instant.now()))) {
            throw new IllegalStateException(
                "Cannot register for an event that has already started"
            );
        }

        if (
            registrationRepository.findByUserAndEvent(user, event).isPresent()
        ) {
            throw new IllegalStateException(
                "User is already registered for this event"
            );
        }

        if (event.getParticipantsCount() >= event.getCapacity()) {
            throw new IllegalStateException("Event is at capacity");
        }

        Registration registration = new Registration(event, user);
        registrationRepository.save(registration);

        event.setParticipantsCount(event.getParticipantsCount() + 1);
        eventRepository.save(event);
    }

    @Transactional
    public void unregisterFromEvent(UUID eventId) {
        UserAccount user = userContext.getCurrentUser();
        Event event = eventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getStartTime().before(Timestamp.from(Instant.now()))) {
            throw new IllegalStateException(
                "Cannot unregister from an event that has already started"
            );
        }

        Registration registration = registrationRepository
            .findByUserAndEvent(user, event)
            .orElseThrow(() ->
                new IllegalStateException(
                    "User is not registered for this event"
                )
            );

        registrationRepository.delete(registration);

        event.setParticipantsCount(event.getParticipantsCount() - 1);
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Registration> getUserRegistrations() {
        UserAccount user = userContext.getCurrentUser();
        return registrationRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Registration> getEventRegistrations(UUID eventId) {
        UserAccount currentUser = userContext.getCurrentUser();
        Event event = eventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthedException(
                "Only the event creator can view registrations"
            );
        }

        return registrationRepository.findByEvent(event);
    }
}
