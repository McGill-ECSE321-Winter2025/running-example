package com.example.ecse_321_demo_backend.responses;

import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.InPersonEvent;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Data
public class EventResponse {

    private UUID id;
    private String eventType;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer capacity;
    private String locationOrLink;
    private UUID createdBy;
    private Timestamp createdAt;

    public static EventResponse fromEvent(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setEventType(event.getEventType());
        response.setDescription(event.getDescription());
        response.setStartTime(event.getStart_time());
        response.setEndTime(event.getEnd_time());
        response.setCapacity(event.getCapacity());
        if (event instanceof InPersonEvent) {
            response.setLocationOrLink(((InPersonEvent) event).getLocation());
        } else if (event instanceof OnlineEvent) {
            response.setLocationOrLink(((OnlineEvent) event).getInviteLink());
        }
        response.setCreatedBy(event.getCreated_by().getId());
        response.setCreatedAt(event.getCreated_at());
        return response;
    }
}
