package com.example.ecse_321_demo_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.exceptions.UnauthedException;
import com.example.ecse_321_demo_backend.middleware.UserContext;
import com.example.ecse_321_demo_backend.models.EventType;
import com.example.ecse_321_demo_backend.models.InPersonEvent;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.CreateEventRequest;
import com.example.ecse_321_demo_backend.requests.UpdateEventRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private EventService eventService;

    private UserAccount testUser;
    private CreateEventRequest createRequest;
    private UpdateEventRequest updateRequest;
    private OnlineEvent testOnlineEvent;
    private InPersonEvent testInPersonEvent;

    @BeforeEach
    void setUp() {
        testUser = new UserAccount("testuser", "password");
        testUser.setId(UUID.randomUUID());

        createRequest = new CreateEventRequest();
        createRequest.setEventType(EventType.ONLINE);
        createRequest.setDescription("Test Event");
        createRequest.setStartTime(
            Timestamp.from(Instant.now().plusSeconds(3600))
        );
        createRequest.setEndTime(
            Timestamp.from(Instant.now().plusSeconds(7200))
        );
        createRequest.setLocationOrLink("http://test.com");
        createRequest.setCapacity(10);

        updateRequest = new UpdateEventRequest();
        updateRequest.setDescription("Updated Event");
        updateRequest.setStartTime(
            Timestamp.from(Instant.now().plusSeconds(3600))
        );
        updateRequest.setEndTime(
            Timestamp.from(Instant.now().plusSeconds(7200))
        );
        updateRequest.setLocationOrLink("http://updated.com");
        updateRequest.setCapacity(20);

        testOnlineEvent = new OnlineEvent(
            testUser,
            "Test Online Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test.com",
            10
        );
        testOnlineEvent.setId(UUID.randomUUID());

        testInPersonEvent = new InPersonEvent(
            testUser,
            "Test In-Person Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "Test Location",
            10
        );
        testInPersonEvent.setId(UUID.randomUUID());
    }

    @Test
    void testCreateOnlineEvent_Success() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.save(any(OnlineEvent.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );

        assertDoesNotThrow(() -> eventService.createEvent(createRequest));
        verify(eventRepository).save(any(OnlineEvent.class));
    }

    @Test
    void testCreateInPersonEvent_Success() {
        createRequest.setEventType(EventType.INPERSON);
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.save(any(InPersonEvent.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );

        assertDoesNotThrow(() -> eventService.createEvent(createRequest));
        verify(eventRepository).save(any(InPersonEvent.class));
    }

    @Test
    void testCreateEvent_InvalidTimes() {
        createRequest.setStartTime(
            Timestamp.from(Instant.now().minusSeconds(3600))
        );
        when(userContext.getCurrentUser()).thenReturn(testUser);

        assertThrows(IllegalArgumentException.class, () ->
            eventService.createEvent(createRequest)
        );
    }

    @Test
    void testUpdateEvent_Success() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testOnlineEvent.getId())).thenReturn(
            Optional.of(testOnlineEvent)
        );
        when(eventRepository.save(any(OnlineEvent.class))).thenReturn(
            testOnlineEvent
        );

        assertDoesNotThrow(() ->
            eventService.updateEvent(testOnlineEvent.getId(), updateRequest)
        );
        verify(eventRepository).save(any(OnlineEvent.class));
    }

    @Test
    void testUpdateEvent_NotCreator() {
        UserAccount otherUser = new UserAccount("other", "password");
        otherUser.setId(UUID.randomUUID());

        when(userContext.getCurrentUser()).thenReturn(otherUser);
        when(eventRepository.findById(testOnlineEvent.getId())).thenReturn(
            Optional.of(testOnlineEvent)
        );

        assertThrows(UnauthedException.class, () ->
            eventService.updateEvent(testOnlineEvent.getId(), updateRequest)
        );
    }

    @Test
    void testUpdateEvent_ReduceCapacityBelowParticipants() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testOnlineEvent.getId())).thenReturn(
            Optional.of(testOnlineEvent)
        );

        testOnlineEvent.setParticipantsCount(15);
        updateRequest.setCapacity(10);

        assertThrows(IllegalArgumentException.class, () ->
            eventService.updateEvent(testOnlineEvent.getId(), updateRequest)
        );
    }
}
