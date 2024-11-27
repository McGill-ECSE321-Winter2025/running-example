package com.example.ecse_321_demo_backend.service;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.exceptions.InvalidCredentialsException;
import com.example.ecse_321_demo_backend.exceptions.UsernameTakenException;
import com.example.ecse_321_demo_backend.models.UserAccount;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

  @Autowired
  private UserAccountRepository userAccountRepository;

  @Transactional
  public void createUserAccount(String username, String password) {
    String cleanUsername = StringUtils.trimToNull(username);
    String cleanPassword = StringUtils.trimToNull(password);

    if (cleanUsername == null) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (cleanPassword == null) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    if (userAccountRepository.findByUsername(cleanUsername).isPresent()) {
      throw new UsernameTakenException(cleanUsername);
    }

    UserAccount newUser = new UserAccount(cleanUsername, cleanPassword);

    userAccountRepository.save(newUser);
  }

  @Transactional
  public void deleteUserAccount(UUID userId) {
    UserAccount user = userAccountRepository
      .findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    userAccountRepository.delete(user);
  }

  @Transactional
  public UserAccount login(String username, String password) {
    String cleanUsername = StringUtils.trimToNull(username);
    String cleanPassword = StringUtils.trimToNull(password);

    if (cleanUsername == null || cleanPassword == null) {
      throw new IllegalArgumentException(
        "Username and password cannot be empty"
      );
    }

    UserAccount user = userAccountRepository
      .findByUsername(cleanUsername)
      .orElseThrow(InvalidCredentialsException::new);

    if (!user.getPassword().equals(cleanPassword)) {
      throw new InvalidCredentialsException();
    }

    return user;
  }
}
