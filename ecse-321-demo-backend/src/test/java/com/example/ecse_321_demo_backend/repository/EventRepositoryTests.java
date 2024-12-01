package com.example.ecse_321_demo_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.models.InPersonEvent;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.UserAccount;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EventRepositoryTests {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    private UserAccount testUser;

    @BeforeEach
    public void setup() {
        testUser = new UserAccount("testuser", "password123");
        userAccountRepository.save(testUser);
    }

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadOnlineEvent() {
        OnlineEvent event = new OnlineEvent(
            testUser,
            "Test Online Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test.com",
            10
        );

        eventRepository.save(event);

        OnlineEvent found = (OnlineEvent) eventRepository
            .findById(event.getId())
            .orElse(null);

        assertNotNull(found);
        assertEquals("Test Online Event", found.getDescription());
        assertEquals("http://test.com", found.getInviteLink());
        assertEquals(testUser.getId(), found.getCreated_by().getId());
    }

    @Test
    public void testPersistAndLoadInPersonEvent() {
        InPersonEvent event = new InPersonEvent(
            testUser,
            "Test In-Person Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "Test Location",
            10
        );

        eventRepository.save(event);

        InPersonEvent found = (InPersonEvent) eventRepository
            .findById(event.getId())
            .orElse(null);

        assertNotNull(found);
        assertEquals("Test In-Person Event", found.getDescription());
        assertEquals("Test Location", found.getLocation());
        assertEquals(testUser.getId(), found.getCreated_by().getId());
    }

    @Test
    public void testDeleteEvent() {
        OnlineEvent event = new OnlineEvent(
            testUser,
            "Test Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test.com",
            10
        );

        eventRepository.save(event);
        assertTrue(eventRepository.findById(event.getId()).isPresent());

        eventRepository.delete(event);
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    public void testUpdateEvent() {
        OnlineEvent event = new OnlineEvent(
            testUser,
            "Original Description",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://original.com",
            10
        );

        eventRepository.save(event);

        event.setDescription("Updated Description");
        event.setInviteLink("http://updated.com");
        eventRepository.save(event);

        OnlineEvent updated = (OnlineEvent) eventRepository
            .findById(event.getId())
            .orElse(null);

        assertNotNull(updated);
        assertEquals("Updated Description", updated.getDescription());
        assertEquals("http://updated.com", updated.getInviteLink());
    }
}
