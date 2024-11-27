package com.example.ecse_321_demo_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.exceptions.InvalidCredentialsException;
import com.example.ecse_321_demo_backend.exceptions.UsernameTakenException;
import com.example.ecse_321_demo_backend.models.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTests {

  @Mock
  private UserAccountRepository userAccountRepository;

  @InjectMocks
  private UserAccountService userAccountService;

  private static final String VALID_USERNAME = "testuser";
  private static final String VALID_PASSWORD = "password123";

  @Test
  public void testCreateUserAccount_Success() {
    when(userAccountRepository.findByUsername(VALID_USERNAME)).thenReturn(
      Optional.empty()
    );
    when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(
      invocation -> invocation.getArgument(0)
    );

    assertDoesNotThrow(() ->
      userAccountService.createUserAccount(VALID_USERNAME, VALID_PASSWORD)
    );

    verify(userAccountRepository).findByUsername(VALID_USERNAME);
    verify(userAccountRepository).save(any(UserAccount.class));
  }

  @Test
  public void testCreateUserAccount_UsernameTaken() {
    when(userAccountRepository.findByUsername(VALID_USERNAME)).thenReturn(
      Optional.of(new UserAccount(VALID_USERNAME, VALID_PASSWORD))
    );

    assertThrows(UsernameTakenException.class, () ->
      userAccountService.createUserAccount(VALID_USERNAME, VALID_PASSWORD)
    );
  }

  @Test
  public void testLogin_Success() {
    UserAccount user = new UserAccount(VALID_USERNAME, VALID_PASSWORD);
    when(userAccountRepository.findByUsername(VALID_USERNAME)).thenReturn(
      Optional.of(user)
    );

    UserAccount loggedInUser = userAccountService.login(
      VALID_USERNAME,
      VALID_PASSWORD
    );
    assertNotNull(loggedInUser);
    assertEquals(VALID_USERNAME, loggedInUser.getUsername());
  }

  @Test
  public void testLogin_InvalidCredentials() {
    when(userAccountRepository.findByUsername(VALID_USERNAME)).thenReturn(
      Optional.empty()
    );

    assertThrows(InvalidCredentialsException.class, () ->
      userAccountService.login(VALID_USERNAME, VALID_PASSWORD)
    );
  }

  @Test
  public void testDeleteUserAccount_Success() {
    UUID userId = UUID.randomUUID();
    UserAccount user = new UserAccount(VALID_USERNAME, VALID_PASSWORD);
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));

    assertDoesNotThrow(() -> userAccountService.deleteUserAccount(userId));
    verify(userAccountRepository).delete(user);
  }

  @Test
  public void testDeleteUserAccount_UserNotFound() {
    UUID userId = UUID.randomUUID();
    when(userAccountRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () ->
      userAccountService.deleteUserAccount(userId)
    );
  }
}
