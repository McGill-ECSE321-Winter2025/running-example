package com.example.ecse_321_demo_backend.config;

import com.example.ecse_321_demo_backend.middleware.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserAuthInterceptor userAuthenticationInterceptor;

    @Autowired
    public WebMvcConfig(UserAuthInterceptor userAuthenticationInterceptor) {
        this.userAuthenticationInterceptor = userAuthenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthenticationInterceptor);
    }
}
