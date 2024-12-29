package com.example.ecse_321_demo_backend.controller;

import com.example.ecse_321_demo_backend.middleware.RequireUser;
import com.example.ecse_321_demo_backend.responses.RegistrationResponse;
import com.example.ecse_321_demo_backend.service.RegistrationService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registrations")
@RequireUser
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<?> registerForEvent(@PathVariable UUID eventId) {
        registrationService.registerForEvent(eventId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> unregisterFromEvent(@PathVariable UUID eventId) {
        registrationService.unregisterFromEvent(eventId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<RegistrationResponse>> getUserRegistrations() {
        List<RegistrationResponse> registrations = registrationService
            .getUserRegistrations()
            .stream()
            .map(RegistrationResponse::fromRegistration)
            .toList();

        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<RegistrationResponse>> getEventRegistrations(
        @PathVariable UUID eventId
    ) {
        List<RegistrationResponse> registrations = registrationService
            .getEventRegistrations(eventId)
            .stream()
            .map(RegistrationResponse::fromRegistration)
            .toList();

        return ResponseEntity.ok(registrations);
    }
}
