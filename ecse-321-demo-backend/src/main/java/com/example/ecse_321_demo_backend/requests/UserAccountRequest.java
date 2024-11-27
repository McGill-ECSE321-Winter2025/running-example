package com.example.ecse_321_demo_backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountRequest {

    private String username;

    private String password;
}
