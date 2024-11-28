package com.example.ecse_321_demo_backend.middleware;

import com.example.ecse_321_demo_backend.models.UserAccount;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class UserContext {

    private UserAccount currentUser;

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount user) {
        this.currentUser = user;
    }

    public void clear() {
        this.currentUser = null;
    }
}
