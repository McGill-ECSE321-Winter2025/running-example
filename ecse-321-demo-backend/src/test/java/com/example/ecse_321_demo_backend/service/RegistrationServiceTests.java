package com.example.ecse_321_demo_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.dao.RegistrationRepository;
import com.example.ecse_321_demo_backend.exceptions.UnauthedException;
import com.example.ecse_321_demo_backend.middleware.UserContext;
import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.Registration;
import com.example.ecse_321_demo_backend.models.UserAccount;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private RegistrationService registrationService;

    private UserAccount testUser;
    private Event testEvent;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testUser = new UserAccount("testuser", "password");
        testUser.setId(UUID.randomUUID());

        testEvent = new OnlineEvent(
            testUser,
            "Test Event",
            Timestamp.from(Instant.now().plusSeconds(3600)), // 1 hour from now
            Timestamp.from(Instant.now().plusSeconds(7200)), // 2 hours from now
            "http://test.com",
            10
        );
        testEvent.setId(UUID.randomUUID());

        testRegistration = new Registration(testEvent, testUser);
    }

    @Test
    void testRegisterForEvent_Success() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );
        when(
            registrationRepository.findByUserAndEvent(testUser, testEvent)
        ).thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
            registrationService.registerForEvent(testEvent.getId())
        );

        verify(registrationRepository).save(any(Registration.class));
        verify(eventRepository).save(testEvent);
        assertEquals(1, testEvent.getParticipantsCount());
    }

    @Test
    void testRegisterForEvent_AlreadyStarted() {
        testEvent.setStartTime(
            Timestamp.from(Instant.now().minusSeconds(3600))
        );
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );

        assertThrows(IllegalStateException.class, () ->
            registrationService.registerForEvent(testEvent.getId())
        );
    }

    @Test
    void testRegisterForEvent_AlreadyRegistered() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );
        when(
            registrationRepository.findByUserAndEvent(testUser, testEvent)
        ).thenReturn(Optional.of(testRegistration));

        assertThrows(IllegalStateException.class, () ->
            registrationService.registerForEvent(testEvent.getId())
        );
    }

    @Test
    void testRegisterForEvent_AtCapacity() {
        testEvent.setParticipantsCount(testEvent.getCapacity());
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );

        assertThrows(IllegalStateException.class, () ->
            registrationService.registerForEvent(testEvent.getId())
        );
    }

    @Test
    void testUnregisterFromEvent_Success() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );
        when(
            registrationRepository.findByUserAndEvent(testUser, testEvent)
        ).thenReturn(Optional.of(testRegistration));

        assertDoesNotThrow(() ->
            registrationService.unregisterFromEvent(testEvent.getId())
        );

        verify(registrationRepository).delete(testRegistration);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void testUnregisterFromEvent_AlreadyStarted() {
        testEvent.setStartTime(
            Timestamp.from(Instant.now().minusSeconds(3600))
        );
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );

        assertThrows(IllegalStateException.class, () ->
            registrationService.unregisterFromEvent(testEvent.getId())
        );
    }

    @Test
    void testGetUserRegistrations() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(registrationRepository.findByUser(testUser)).thenReturn(
            Arrays.asList(testRegistration)
        );

        List<Registration> registrations =
            registrationService.getUserRegistrations();

        assertFalse(registrations.isEmpty());
        assertEquals(1, registrations.size());
        assertEquals(testRegistration, registrations.get(0));
    }

    @Test
    void testGetEventRegistrations_AsCreator() {
        when(userContext.getCurrentUser()).thenReturn(testUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );
        when(registrationRepository.findByEvent(testEvent)).thenReturn(
            Arrays.asList(testRegistration)
        );

        List<Registration> registrations =
            registrationService.getEventRegistrations(testEvent.getId());

        assertFalse(registrations.isEmpty());
        assertEquals(1, registrations.size());
        assertEquals(testRegistration, registrations.get(0));
    }

    @Test
    void testGetEventRegistrations_NotCreator() {
        UserAccount otherUser = new UserAccount("other", "password");
        otherUser.setId(UUID.randomUUID());

        when(userContext.getCurrentUser()).thenReturn(otherUser);
        when(eventRepository.findById(testEvent.getId())).thenReturn(
            Optional.of(testEvent)
        );

        assertThrows(UnauthedException.class, () ->
            registrationService.getEventRegistrations(testEvent.getId())
        );
    }
}
