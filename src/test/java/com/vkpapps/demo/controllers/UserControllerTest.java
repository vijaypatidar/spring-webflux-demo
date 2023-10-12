package com.vkpapps.demo.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ContentDisposition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient()
class UserControllerTest extends AbstractControllerTest {
  @MockBean
  private UserService userService;

  @Test
  void testGetUserInfo() {
    User user = getAdminUser();
    String jwtToken = getJwtToken(user);
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

    webClient.get()
        .uri("/api/users")
        .header("Authorization", "Bearer " + jwtToken)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.username").isEqualTo(user.getUsername())
        .jsonPath("$.email").isEqualTo(user.getEmail())
        .jsonPath("$.roles").isEqualTo(user.getRoles().get(0));

    verify(userService, Mockito.times(1)).getUsername("vijaypatidar");
  }

  @Test
  void testGetUserInfoByUsername() {
    User user = getAdminUser();
    String username = user.getUsername();
    String jwtToken = getJwtToken(user);
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

    webClient.get()
        .uri("/api/users/" + username)
        .header("Authorization", "Bearer " + jwtToken)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.username").isEqualTo(user.getUsername())
        .jsonPath("$.email").isEqualTo(user.getEmail())
        .jsonPath("$.roles").isEqualTo(user.getRoles().get(0));

    verify(userService, Mockito.times(1)).getUsername(username);
  }

  @Test
  void testGetUserInfoByUsernameFailsDueTo403() {
    User user = getNormalUser();
    String username = user.getUsername();
    String jwtToken = getJwtToken(user);
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

    webClient.get()
        .uri("/api/users/" + username)
        .header("Authorization", "Bearer " + jwtToken)
        .exchange()
        .expectStatus().isForbidden();

    verify(userService, Mockito.times(0)).getUsername(username);
  }

  @Test
  void testGetUserInfoWithInvalidJwtToken() {

    User user = getAdminUser();
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

    webClient.get()
        .uri("/api/users")
        .header("Authorization", "Bearer " + "fjsdfhsdcsdfsldhfsdfjssvks")
        .exchange()
        .expectStatus().isUnauthorized();

    verify(userService, Mockito.times(0)).getUsername("vijaypatidar");
  }

  @Test
  void verifyExport() {
    User user = getAdminUser();
    String jwtToken = getJwtToken(user);
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));
    when(userService.getUsers()).thenReturn(Flux.just(user));

    webClient.get()
        .uri("/api/users/export/csv")
        .header("Authorization", "Bearer " + jwtToken)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentDisposition(ContentDisposition
            .attachment()
            .filename("users.csv")
            .build());

    verify(userService, Mockito.times(1)).getUsers();

  }

  @Test
  @DisplayName("Test export user fails due to invalid role")
  void testExportForbidden() {
    User user = getNormalUser();
    String jwtToken = getJwtToken(user);
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));
    when(userService.getUsers()).thenReturn(Flux.just(user));

    webClient.get()
        .uri("/api/users/export/csv")
        .header("Authorization", "Bearer " + jwtToken)
        .exchange()
        .expectStatus().isForbidden();

    verify(userService, Mockito.times(0)).getUsers();

  }

  @Test
  @DisplayName("Test unsupported export type")
  void testUnsupportedExport() {
    User user = getAdminUser();
    String jwtToken = getJwtToken(user);
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));
    when(userService.getUsers()).thenReturn(Flux.just(user));

    webClient.get()
        .uri("/api/users/export/ppt")
        .header("Authorization", "Bearer " + jwtToken)
        .exchange()
        .expectStatus().isBadRequest();

    verify(userService, Mockito.times(0)).getUsers();

  }
}