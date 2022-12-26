package com.vkpapps.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.security.jwt.JwtTokenProvider;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class AbstractControllerTest {
    @Autowired
    protected WebTestClient webClient;
    @Autowired
    protected PasswordEncoder encoder;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider tokenProvider;

    protected User getAdminUser() {
        return new User("vijaypatidar",
                encoder.encode("12345678"),
                "vijay@example.com",
                List.of("ROLE_ADMIN"),
                false
        );
    }
    protected User getNormalUser() {
        return new User("vijaypatidar",
                encoder.encode("12345678"),
                "vijay@example.com",
                List.of("ROLE_USER"),
                false
        );
    }

    protected String getJwtToken(User user) {
        return tokenProvider.createToken(user);
    }
}
