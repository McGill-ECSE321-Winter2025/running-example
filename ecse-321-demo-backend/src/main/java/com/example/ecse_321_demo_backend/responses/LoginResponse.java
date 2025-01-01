package com.example.ecse_321_demo_backend.responses;

import java.util.UUID;
import lombok.Data;

@Data
public class LoginResponse {

    private final UUID userId;

    private final String username;
}
