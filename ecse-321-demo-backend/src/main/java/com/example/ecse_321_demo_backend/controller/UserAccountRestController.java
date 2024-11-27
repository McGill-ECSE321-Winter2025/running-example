package com.example.ecse_321_demo_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecse_321_demo_backend.service.UserAccountService;
import com.example.ecse_321_demo_backend.requests.UserAccountRequest;

@RestController
public class UserAccountRestController {
    @Autowired
    private UserAccountService userAccountService;


}
