package com.example.ecse_321_demo_backend.service;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.exceptions.UnauthedException;
import com.example.ecse_321_demo_backend.middleware.UserContext;
import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.EventType;
import com.example.ecse_321_demo_backend.models.InPersonEvent;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.CreateEventRequest;
import com.example.ecse_321_demo_backend.requests.UpdateEventRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final UserContext userContext;

    @Autowired
    public EventService(
        EventRepository eventRepository,
        UserContext userContext
    ) {
        this.eventRepository = eventRepository;
        this.userContext = userContext;
    }

    @Transactional
    public void createEvent(CreateEventRequest request) {
        UserAccount creator = userContext.getCurrentUser();

        validateEventTimes(request.getStartTime(), request.getEndTime());

        Event event;

        if (request.getEventType() == EventType.ONLINE) {
            event = new OnlineEvent(
                creator,
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getLocationOrLink(),
                request.getCapacity()
            );
        } else {
            event = new InPersonEvent(
                creator,
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getLocationOrLink(),
                request.getCapacity()
            );
        }

        eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(UUID eventId, UpdateEventRequest request) {
        UserAccount currentUser = userContext.getCurrentUser();
        Event event = eventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthedException(
                "Only the creator can update the event"
            );
        }

        validateEventTimes(request.getStartTime(), request.getEndTime());

        Integer currentParticipants = event.getParticipantsCount();

        if (request.getCapacity() < currentParticipants) {
            throw new IllegalArgumentException(
                "New capacity cannot be less than current participant count"
            );
        }

        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setCapacity(request.getCapacity());

        if (event instanceof OnlineEvent) {
            ((OnlineEvent) event).setInviteLink(request.getLocationOrLink());
        } else if (event instanceof InPersonEvent) {
            ((InPersonEvent) event).setLocation(request.getLocationOrLink());
        }

        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEvent(UUID eventId) {
        return eventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    @Transactional(readOnly = true)
    public List<Event> getEvents(
        UUID createdBy,
        Timestamp startTime,
        Timestamp endTime
    ) {
        List<Event> events = (List<Event>) eventRepository.findAll();

        return events
            .stream()
            .filter(
                event ->
                    (createdBy == null ||
                        event.getCreatedBy().getId().equals(createdBy)) &&
                    (startTime == null ||
                        !event.getStartTime().before(startTime)) &&
                    (endTime == null || !event.getEndTime().after(endTime))
            )
            .toList();
    }

    private void validateEventTimes(Timestamp startTime, Timestamp endTime) {
        Timestamp now = Timestamp.from(Instant.now());

        if (startTime.before(now)) {
            throw new IllegalArgumentException(
                "Start time cannot be in the past"
            );
        }

        if (endTime.before(startTime)) {
            throw new IllegalArgumentException(
                "End time must be after start time"
            );
        }
    }
}
