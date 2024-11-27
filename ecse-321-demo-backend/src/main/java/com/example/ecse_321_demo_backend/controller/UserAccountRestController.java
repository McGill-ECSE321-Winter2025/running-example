package com.example.ecse_321_demo_backend.controller;

import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.LoginRequest;
import com.example.ecse_321_demo_backend.requests.UserAccountRequest;
import com.example.ecse_321_demo_backend.responses.LoginResponse;
import com.example.ecse_321_demo_backend.service.UserAccountService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAccountRestController {

  @Autowired
  private UserAccountService userAccountService;

  @PostMapping("/users")
  public ResponseEntity<?> createUserAccount(
    @RequestBody UserAccountRequest request
  ) {
    userAccountService.createUserAccount(
      request.getUsername(),
      request.getPassword()
    );
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/users/{userId}")
  public ResponseEntity<?> deleteUserAccount(@PathVariable UUID userId) {
    userAccountService.deleteUserAccount(userId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
    @RequestBody LoginRequest request
  ) {
    UserAccount user = userAccountService.login(
      request.getUsername(),
      request.getPassword()
    );
    return ResponseEntity.ok(new LoginResponse(user.getId()));
  }
}
