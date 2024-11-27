package com.example.ecse_321_demo_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Username already exists")
public class UsernameTakenException extends RuntimeException {

  public UsernameTakenException(String username) {
    super("Username '" + username + "' already in use");
  }
}
