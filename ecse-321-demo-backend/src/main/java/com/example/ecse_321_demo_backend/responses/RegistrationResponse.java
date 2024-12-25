package com.example.ecse_321_demo_backend.responses;

import com.example.ecse_321_demo_backend.models.Registration;
import java.util.UUID;
import lombok.Data;

@Data
public class RegistrationResponse {

    private UUID userId;
    private String username;
    private UUID eventId;
    private String eventDescription;

    public static RegistrationResponse fromRegistration(
        Registration registration
    ) {
        RegistrationResponse response = new RegistrationResponse();
        response.setUserId(registration.getUser().getId());
        response.setUsername(
            registration.getEvent().getCreated_by().getUsername()
        );
        response.setEventId(registration.getEvent().getId());
        response.setEventDescription(registration.getEvent().getDescription());
        return response;
    }
}
