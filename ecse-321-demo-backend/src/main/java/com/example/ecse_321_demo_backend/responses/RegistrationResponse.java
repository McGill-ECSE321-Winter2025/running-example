package com.example.ecse_321_demo_backend.responses;

import com.example.ecse_321_demo_backend.models.Registration;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Data
public class RegistrationResponse {

    private UUID userId;
    private String username;
    private String creatorUsername;
    private UUID eventId;
    private String eventDescription;
    private Timestamp registeredAt;

    public static RegistrationResponse fromRegistration(
        Registration registration
    ) {
        RegistrationResponse response = new RegistrationResponse();
        response.setUserId(registration.getUser().getId());
        response.setUsername(registration.getUser().getUsername());
        response.setCreatorUsername(
            registration.getEvent().getCreated_by().getUsername()
        );
        response.setEventId(registration.getEvent().getId());
        response.setEventDescription(registration.getEvent().getDescription());
        response.setRegisteredAt(registration.getRegisteredAt());
        return response;
    }
}
