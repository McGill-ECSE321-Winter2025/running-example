package com.example.ecse_321_demo_backend.requests;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class UpdateEventRequest {

    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer capacity;
    private String locationOrLink;
}
