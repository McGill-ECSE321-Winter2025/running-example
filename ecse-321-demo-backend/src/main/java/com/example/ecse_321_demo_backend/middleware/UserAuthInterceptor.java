package com.example.ecse_321_demo_backend.middleware;

import com.example.ecse_321_demo_backend.dao.UserAccountRepository;
import com.example.ecse_321_demo_backend.exceptions.UnauthedException;
import com.example.ecse_321_demo_backend.models.UserAccount;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    private final UserAccountRepository userAccountRepository;

    private final UserContext userContext;

    @Autowired
    public UserAuthInterceptor(
        UserAccountRepository userAccountRepository,
        UserContext userContext
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userContext = userContext;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws UnauthedException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        RequireUser requireUser = handlerMethod.getMethodAnnotation(
            RequireUser.class
        );

        if (requireUser == null) {
            requireUser = handlerMethod
                .getBeanType()
                .getAnnotation(RequireUser.class);
        }

        if (requireUser != null) {
            String userIdHeader = request.getHeader("User-Id");

            if (userIdHeader == null) {
                throw new UnauthedException("No User-Id header provided");
            }

            try {
                UUID userId = UUID.fromString(userIdHeader);

                UserAccount user = userAccountRepository
                    .findById(userId)
                    .orElseThrow(() -> new UnauthedException("User not found"));

                userContext.setCurrentUser(user);
            } catch (IllegalArgumentException e) {
                throw new UnauthedException("Invalid User-Id format");
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception ex
    ) {
        userContext.clear();
    }
}
