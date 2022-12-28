package com.vkpapps.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkpapps.demo.AbstractTestData;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class AbstractControllerTest extends AbstractTestData {
    @Autowired
    protected WebTestClient webClient;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider tokenProvider;

    protected String getJwtToken(User user) {
        return tokenProvider.createToken(user);
    }
}
