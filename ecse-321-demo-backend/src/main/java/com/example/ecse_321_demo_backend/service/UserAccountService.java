package com.example.ecse_321_demo_backend.service;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.exceptions.InvalidCredentialsException;
import com.example.ecse_321_demo_backend.exceptions.UsernameTakenException;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.requests.AuthRequest;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public void createUserAccount(AuthRequest request) {
        validateUsernameAndPassword(
            request.getUsername(),
            request.getPassword()
        );

        if (
            userAccountRepository
                .findByUsername(request.getUsername())
                .isPresent()
        ) {
            throw new UsernameTakenException(request.getUsername());
        }

        UserAccount newUser = new UserAccount(
            request.getUsername(),
            request.getPassword()
        );
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
    public UserAccount login(AuthRequest request) {
        validateUsernameAndPassword(
            request.getUsername(),
            request.getPassword()
        );

        UserAccount user = userAccountRepository
            .findByUsername(request.getUsername())
            .orElseThrow(InvalidCredentialsException::new);

        if (!user.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    private void validateUsernameAndPassword(String username, String password) {
        String cleanUsername = StringUtils.trimToNull(username);
        String cleanPassword = StringUtils.trimToNull(password);

        if (cleanUsername == null) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (cleanPassword == null) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
