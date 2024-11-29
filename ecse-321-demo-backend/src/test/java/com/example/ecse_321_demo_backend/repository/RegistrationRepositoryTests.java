package com.example.ecse_321_demo_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.dao.RegistrationRepository;
import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.Registration;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.models.keys.RegistrationId;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RegistrationRepositoryTests {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EventRepository eventRepository;

    private UserAccount testUser;
    private OnlineEvent testEvent;
    private Registration testRegistration;

    @BeforeEach
    public void setup() {
        testUser = new UserAccount("testuser", "password123");
        userAccountRepository.save(testUser);

        testEvent = new OnlineEvent(
            testUser,
            "Test Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test.com",
            10
        );
        eventRepository.save(testEvent);

        testRegistration = new Registration(testEvent, testUser);
        registrationRepository.save(testRegistration);
    }

    @AfterEach
    public void cleanup() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadRegistration() {
        RegistrationId id = new RegistrationId(
            testEvent.getId(),
            testUser.getId()
        );
        Optional<Registration> foundRegistration =
            registrationRepository.findById(id);

        assertTrue(foundRegistration.isPresent());
        assertEquals(
            testUser.getId(),
            foundRegistration.get().getUser().getId()
        );
        assertEquals(
            testEvent.getId(),
            foundRegistration.get().getEvent().getId()
        );
    }

    @Test
    public void testFindByUser() {
        List<Registration> userRegistrations =
            registrationRepository.findByUser(testUser);

        assertFalse(userRegistrations.isEmpty());
        assertEquals(1, userRegistrations.size());
        assertEquals(
            testEvent.getId(),
            userRegistrations.get(0).getEvent().getId()
        );
    }

    @Test
    public void testFindByEvent() {
        List<Registration> eventRegistrations =
            registrationRepository.findByEvent(testEvent);

        assertFalse(eventRegistrations.isEmpty());
        assertEquals(1, eventRegistrations.size());
        assertEquals(
            testUser.getId(),
            eventRegistrations.get(0).getUser().getId()
        );
    }

    @Test
    public void testFindByUserAndEvent() {
        Optional<Registration> registration =
            registrationRepository.findByUserAndEvent(testUser, testEvent);

        assertTrue(registration.isPresent());
        assertEquals(testUser.getId(), registration.get().getUser().getId());
        assertEquals(testEvent.getId(), registration.get().getEvent().getId());
    }

    @Test
    public void testDeleteRegistration() {
        registrationRepository.delete(testRegistration);

        RegistrationId id = new RegistrationId(
            testEvent.getId(),
            testUser.getId()
        );
        Optional<Registration> deletedRegistration =
            registrationRepository.findById(id);

        assertFalse(deletedRegistration.isPresent());
    }

    @Test
    public void testMultipleRegistrationsForUser() {
        OnlineEvent secondEvent = new OnlineEvent(
            testUser,
            "Second Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test2.com",
            10
        );
        eventRepository.save(secondEvent);

        Registration secondRegistration = new Registration(
            secondEvent,
            testUser
        );
        registrationRepository.save(secondRegistration);

        List<Registration> userRegistrations =
            registrationRepository.findByUser(testUser);

        assertEquals(2, userRegistrations.size());
    }

    @Test
    public void testMultipleUsersForEvent() {
        UserAccount secondUser = new UserAccount("testuser2", "password123");
        userAccountRepository.save(secondUser);

        Registration secondRegistration = new Registration(
            testEvent,
            secondUser
        );
        registrationRepository.save(secondRegistration);

        List<Registration> eventRegistrations =
            registrationRepository.findByEvent(testEvent);

        assertEquals(2, eventRegistrations.size());
    }

    @Test
    public void testNonExistentRegistration() {
        UserAccount otherUser = new UserAccount("otheruser", "password123");
        userAccountRepository.save(otherUser);

        OnlineEvent otherEvent = new OnlineEvent(
            testUser,
            "Other Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://other.com",
            10
        );
        eventRepository.save(otherEvent);

        Optional<Registration> nonExistentRegistration =
            registrationRepository.findByUserAndEvent(otherUser, otherEvent);

        assertFalse(nonExistentRegistration.isPresent());
    }
}
