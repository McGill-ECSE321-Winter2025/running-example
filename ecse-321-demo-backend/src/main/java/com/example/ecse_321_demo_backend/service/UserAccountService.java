package com.example.ecse_321_demo_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRespository;

    public void createUserAccount(String username, String password) {

    }

}
