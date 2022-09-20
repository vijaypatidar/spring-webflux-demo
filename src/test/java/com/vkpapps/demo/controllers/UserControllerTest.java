package com.vkpapps.demo.controllers;

import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient()
class UserControllerTest extends AbstractControllerTest{
    @MockBean
    private UserService userService;

    @Test
    void testGetUserInfo(){

        User user = getUser1();
        String jwtToken = getJwtToken(user);
        Mockito.when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

        webClient.get()
                .uri("/api/users")
                .header("Authorization","Bearer "+jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo(user.getUsername())
                .jsonPath("$.email").isEqualTo(user.getEmail())
                .jsonPath("$.role").isEqualTo(user.getRole());

        Mockito.verify(userService, Mockito.times(1)).getUsername("vijaypatidar");
    }

    @Test
    void testGetUserInfoWithInvalidJwtToken(){

        User user = getUser1();
        Mockito.when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

        webClient.get()
                .uri("/api/users")
                .header("Authorization","Bearer "+"fjsdfhsdcsdfsldhfsdfjssvks")
                .exchange()
                .expectStatus().isUnauthorized();

        Mockito.verify(userService, Mockito.times(0)).getUsername("vijaypatidar");
    }
}