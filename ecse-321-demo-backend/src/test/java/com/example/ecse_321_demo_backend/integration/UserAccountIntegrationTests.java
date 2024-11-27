package com.example.ecse_321_demo_backend.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.LoginRequest;
import com.example.ecse_321_demo_backend.requests.UserAccountRequest;
import com.example.ecse_321_demo_backend.responses.LoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserAccountIntegrationTests {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @AfterEach
  public void clearDatabase() {
    userAccountRepository.deleteAll();
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  public void testCreateUserAccount() {
    UserAccountRequest request = new UserAccountRequest();
    request.setUsername("newuser");
    request.setPassword("password123");

    ResponseEntity<?> response = restTemplate.postForEntity(
      createURLWithPort("/users"),
      request,
      Void.class
    );

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(userAccountRepository.findByUsername("newuser").isPresent());
  }

  @Test
  public void testLoginSuccess() {
    UserAccount user = new UserAccount("loginuser", "password123");
    userAccountRepository.save(user);

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("loginuser");
    loginRequest.setPassword("password123");

    ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
      createURLWithPort("/login"),
      loginRequest,
      LoginResponse.class
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getUserId());
  }

  @Test
  public void testDeleteUser() {
    UserAccount user = new UserAccount("deleteuser", "password123");
    userAccountRepository.save(user);

    restTemplate.delete(createURLWithPort("/users/" + user.getId()));

    assertFalse(userAccountRepository.findById(user.getId()).isPresent());
  }

  @Test
  public void testCreateDuplicateUsername() {
    UserAccount user = new UserAccount("duplicate", "password123");
    userAccountRepository.save(user);

    UserAccountRequest request = new UserAccountRequest();
    request.setUsername("duplicate");
    request.setPassword("differentpassword");

    ResponseEntity<?> response = restTemplate.postForEntity(
      createURLWithPort("/users"),
      request,
      Void.class
    );

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }
}
