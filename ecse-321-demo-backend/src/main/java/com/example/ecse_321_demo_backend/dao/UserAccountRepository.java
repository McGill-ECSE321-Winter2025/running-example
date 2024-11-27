package com.example.ecse_321_demo_backend.dao;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.ecse_321_demo_backend.models.UserAccount;

public interface UserAccountRepository extends CrudRepository<UserAccount, UUID> {
    Optional<UserAccount> findById(UUID id);
    Optional<UserAccount> findByUsername(String username);
}
