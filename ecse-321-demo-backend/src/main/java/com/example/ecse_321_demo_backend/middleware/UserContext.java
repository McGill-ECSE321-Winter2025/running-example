package com.example.ecse_321_demo_backend.middleware;

import com.example.ecse_321_demo_backend.models.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class UserContext {

    private UserAccount currentUser;

    public void clear() {
        this.currentUser = null;
    }
}
