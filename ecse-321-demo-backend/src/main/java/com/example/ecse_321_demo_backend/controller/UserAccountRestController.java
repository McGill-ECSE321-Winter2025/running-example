package com.example.ecse_321_demo_backend.controller;

import com.example.ecse_321_demo_backend.middleware.RequireUser;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.AuthRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserAccountRestController {

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountRestController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    public ResponseEntity<?> createUserAccount(
        @RequestBody AuthRequest request
    ) {
        userAccountService.createUserAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}")
    @RequireUser
    public ResponseEntity<?> deleteUserAccount(@PathVariable UUID userId) {
        userAccountService.deleteUserAccount(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @RequestBody AuthRequest request
    ) {
        UserAccount user = userAccountService.login(request);
        return ResponseEntity.ok(
            new LoginResponse(user.getId(), user.getUsername())
        );
    }
}
