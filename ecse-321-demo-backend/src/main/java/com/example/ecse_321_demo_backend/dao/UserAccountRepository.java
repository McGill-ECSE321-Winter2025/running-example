package com.example.ecse_321_demo_backend.dao;

import com.example.ecse_321_demo_backend.models.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserAccountRepository
    extends CrudRepository<UserAccount, UUID> {
    Optional<UserAccount> findById(UUID id);
    Optional<UserAccount> findByUsername(String username);
}
