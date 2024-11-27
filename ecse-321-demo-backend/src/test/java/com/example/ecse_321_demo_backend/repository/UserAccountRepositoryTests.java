package com.example.ecse_321_demo_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.models.UserAccount;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserAccountRepositoryTests {

  @Autowired
  private UserAccountRepository userAccountRepository;

  @AfterEach
  public void clearDatabase() {
    userAccountRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadUserAccount() {
    String username = "testuser";
    String password = "password123";
    UserAccount user = new UserAccount(username, password);

    userAccountRepository.save(user);

    UserAccount fetchedById = userAccountRepository
      .findById(user.getId())
      .orElse(null);

    assertNotNull(fetchedById);
    assertEquals(username, fetchedById.getUsername());
    assertEquals(password, fetchedById.getPassword());

    Optional<UserAccount> fetchedByUsername =
      userAccountRepository.findByUsername(username);

    assertTrue(fetchedByUsername.isPresent());
    assertEquals(user.getId(), fetchedByUsername.get().getId());
  }

  @Test
  public void testDeleteUserAccount() {
    UserAccount user = new UserAccount("deletetest", "password123");
    userAccountRepository.save(user);

    assertTrue(userAccountRepository.findById(user.getId()).isPresent());

    userAccountRepository.delete(user);

    assertFalse(userAccountRepository.findById(user.getId()).isPresent());
  }
}
