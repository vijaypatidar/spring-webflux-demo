package com.vkpapps.demo.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vkpapps.demo.health.DeepHealthChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient()
class HealthControllerTest extends AbstractControllerTest {

  @MockBean
  DeepHealthChecker deepHealthCheckerMock;

  @Test
  void check_success() {
    when(deepHealthCheckerMock.isHealthy()).thenReturn(Mono.just(true));
    webClient.get()
        .uri("/health")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.serverId").isNotEmpty();
    verify(deepHealthCheckerMock).isHealthy();
  }

  @Test
  void check_fails() {
    when(deepHealthCheckerMock.isHealthy()).thenReturn(Mono.just(false));
    webClient.get()
        .uri("/health")
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.messages").isNotEmpty();
    verify(deepHealthCheckerMock).isHealthy();
  }

}