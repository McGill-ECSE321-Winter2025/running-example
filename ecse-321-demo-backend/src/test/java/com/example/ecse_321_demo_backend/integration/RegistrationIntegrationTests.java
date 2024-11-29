package com.example.ecse_321_demo_backend.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.dao.RegistrationRepository;
import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.responses.RegistrationResponse;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    private UserAccount testUser;
    private OnlineEvent testEvent;
    private HttpHeaders headers;

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

        headers = new HttpHeaders();
        headers.set("User-Id", testUser.getId().toString());
    }

    @AfterEach
    public void cleanup() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

    @Test
    public void testRegisterForEvent() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(
            registrationRepository
                .findByUserAndEvent(testUser, testEvent)
                .isPresent()
        );
    }

    @Test
    public void testUnregisterFromEvent() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        ResponseEntity<Void> response = restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.DELETE,
            requestEntity,
            Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(
            registrationRepository
                .findByUserAndEvent(testUser, testEvent)
                .isPresent()
        );
    }

    @Test
    public void testGetUserRegistrations() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        ResponseEntity<RegistrationResponse[]> response = restTemplate.exchange(
            createURLWithPort("/registrations/my-registrations"),
            HttpMethod.GET,
            requestEntity,
            RegistrationResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(
            testEvent.getId().toString(),
            response.getBody()[0].getEventId().toString()
        );
    }

    @Test
    public void testGetEventRegistrations() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        ResponseEntity<RegistrationResponse[]> response = restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.GET,
            requestEntity,
            RegistrationResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(
            testUser.getId().toString(),
            response.getBody()[0].getUserId().toString()
        );
    }

    @Test
    public void testRegisterForFullEvent() {
        testEvent.setParticipantsCount(testEvent.getCapacity());
        eventRepository.save(testEvent);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        assertEquals(
            HttpStatus.INTERNAL_SERVER_ERROR,
            response.getStatusCode()
        );
    }

    @Test
    public void testUnauthorizedAccess() {
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/registrations/" + testEvent.getId()),
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
