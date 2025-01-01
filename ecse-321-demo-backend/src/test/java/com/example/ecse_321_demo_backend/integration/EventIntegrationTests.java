package com.example.ecse_321_demo_backend.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecse_321_demo_backend.dao.EventRepository;
import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.models.EventType;
import com.example.ecse_321_demo_backend.models.OnlineEvent;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.CreateEventRequest;
import com.example.ecse_321_demo_backend.requests.UpdateEventRequest;
import com.example.ecse_321_demo_backend.responses.EventResponse;
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
public class EventIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EventRepository eventRepository;

    private UserAccount testUser;
    private OnlineEvent testEvent;
    private HttpHeaders headers;

    @BeforeEach
    public void setup() {
        testUser = new UserAccount("testuser", "password123");
        userAccountRepository.save(testUser);

        headers = new HttpHeaders();
        headers.set("User-Id", testUser.getId().toString());
    }

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

    @Test
    public void testCreateEvent() {
        CreateEventRequest request = new CreateEventRequest();
        request.setEventType(EventType.ONLINE);
        request.setDescription("Test Event");
        request.setStartTime(Timestamp.from(Instant.now().plusSeconds(3600)));
        request.setEndTime(Timestamp.from(Instant.now().plusSeconds(7200)));
        request.setLocationOrLink("http://test.com");
        request.setCapacity(10);

        HttpEntity<CreateEventRequest> requestEntity = new HttpEntity<>(
            request,
            headers
        );

        ResponseEntity<Void> response = restTemplate.exchange(
            createURLWithPort("/events"),
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateEvent() {
        testEvent = new OnlineEvent(
            testUser,
            "Original Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://original.com",
            10
        );
        eventRepository.save(testEvent);

        UpdateEventRequest request = new UpdateEventRequest();
        request.setDescription("Updated Event");
        request.setStartTime(Timestamp.from(Instant.now().plusSeconds(3600)));
        request.setEndTime(Timestamp.from(Instant.now().plusSeconds(7200)));
        request.setLocationOrLink("http://updated.com");
        request.setCapacity(20);

        HttpEntity<UpdateEventRequest> requestEntity = new HttpEntity<>(
            request,
            headers
        );

        ResponseEntity<EventResponse> response = restTemplate.exchange(
            createURLWithPort("/events/" + testEvent.getId()),
            HttpMethod.PUT,
            requestEntity,
            EventResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Event", response.getBody().getDescription());
        assertEquals(
            "http://updated.com",
            response.getBody().getLocationOrLink()
        );
    }

    @Test
    public void testGetEvents() {
        testEvent = new OnlineEvent(
            testUser,
            "Test Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test.com",
            10
        );
        eventRepository.save(testEvent);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<EventResponse[]> response = restTemplate.exchange(
            createURLWithPort("/events"),
            HttpMethod.GET,
            requestEntity,
            EventResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    public void testGetEventsByCreator() {
        testEvent = new OnlineEvent(
            testUser,
            "Test Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://test.com",
            10
        );
        eventRepository.save(testEvent);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<EventResponse[]> response = restTemplate.exchange(
            createURLWithPort("/events?createdBy=" + testUser.getId()),
            HttpMethod.GET,
            requestEntity,
            EventResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals(
            testUser.getUsername(),
            response.getBody()[0].getCreatedBy()
        );
    }

    @Test
    public void testCreateEventUnauthorized() {
        CreateEventRequest request = new CreateEventRequest();
        request.setEventType(EventType.ONLINE);
        request.setDescription("Test Event");
        request.setStartTime(Timestamp.from(Instant.now().plusSeconds(3600)));
        request.setEndTime(Timestamp.from(Instant.now().plusSeconds(7200)));
        request.setLocationOrLink("http://test.com");
        request.setCapacity(10);

        HttpEntity<CreateEventRequest> requestEntity = new HttpEntity<>(
            request,
            new HttpHeaders()
        );

        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/events"),
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateEventUnauthorized() {
        testEvent = new OnlineEvent(
            testUser,
            "Original Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://original.com",
            10
        );
        eventRepository.save(testEvent);

        UserAccount otherUser = new UserAccount("otheruser", "password123");
        userAccountRepository.save(otherUser);
        HttpHeaders otherHeaders = new HttpHeaders();
        otherHeaders.set("User-Id", otherUser.getId().toString());

        UpdateEventRequest request = new UpdateEventRequest();
        request.setDescription("Updated Event");
        HttpEntity<UpdateEventRequest> requestEntity = new HttpEntity<>(
            request,
            otherHeaders
        );

        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/events/" + testEvent.getId()),
            HttpMethod.PUT,
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testInvalidEventUpdate() {
        testEvent = new OnlineEvent(
            testUser,
            "Original Event",
            Timestamp.from(Instant.now().plusSeconds(3600)),
            Timestamp.from(Instant.now().plusSeconds(7200)),
            "http://original.com",
            10
        );
        testEvent.setParticipantsCount(5);
        eventRepository.save(testEvent);

        UpdateEventRequest request = new UpdateEventRequest();
        request.setDescription("Updated Event");
        request.setStartTime(Timestamp.from(Instant.now().plusSeconds(3600)));
        request.setEndTime(Timestamp.from(Instant.now().plusSeconds(7200)));
        request.setLocationOrLink("http://updated.com");
        request.setCapacity(3); // Less than current participants

        HttpEntity<UpdateEventRequest> requestEntity = new HttpEntity<>(
            request,
            headers
        );

        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/events/" + testEvent.getId()),
            HttpMethod.PUT,
            requestEntity,
            String.class
        );

        assertEquals(
            HttpStatus.INTERNAL_SERVER_ERROR,
            response.getStatusCode()
        );
    }
}
