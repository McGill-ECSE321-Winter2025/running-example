package com.example.ecse_321_demo_backend.controller;

import com.example.ecse_321_demo_backend.middleware.RequireUser;
import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.requests.CreateEventRequest;
import com.example.ecse_321_demo_backend.requests.UpdateEventRequest;
import com.example.ecse_321_demo_backend.responses.EventResponse;
import com.example.ecse_321_demo_backend.service.EventService;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    @RequireUser
    public ResponseEntity<EventResponse> createEvent(
        @RequestBody CreateEventRequest request
    ) {
        eventService.createEvent(request);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{eventId}")
    @RequireUser
    public ResponseEntity<EventResponse> updateEvent(
        @PathVariable UUID eventId,
        @RequestBody UpdateEventRequest request
    ) {
        Event event = eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(EventResponse.fromEvent(event));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID eventId) {
        Event event = eventService.getEvent(eventId);

        return ResponseEntity.ok(EventResponse.fromEvent(event));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents(
        @RequestParam(required = false) UUID createdBy,
        @RequestParam(required = false) Timestamp startTime,
        @RequestParam(required = false) Timestamp endTime
    ) {
        List<EventResponse> events = eventService
            .getEvents(createdBy, startTime, endTime)
            .stream()
            .map(EventResponse::fromEvent)
            .toList();

        return ResponseEntity.ok(events);
    }
}
