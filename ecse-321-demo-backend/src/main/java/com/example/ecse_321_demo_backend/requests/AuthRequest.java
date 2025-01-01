package com.example.ecse_321_demo_backend.requests;

import lombok.Data;

@Data
public class AuthRequest {

    private String username;

    private String password;
}
