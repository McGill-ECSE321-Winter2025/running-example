package com.example.ecse_321_demo_backend.requests;

import com.example.ecse_321_demo_backend.models.EventType;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class CreateEventRequest {

    private EventType eventType;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer capacity;
    private String locationOrLink;
}
