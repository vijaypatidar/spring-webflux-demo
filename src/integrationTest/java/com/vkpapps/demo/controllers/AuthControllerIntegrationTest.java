package com.vkpapps.demo.controllers;

import com.vkpapps.demo.AbstractIntegrationTest;
import com.vkpapps.demo.dtos.auth.OtpRequestDto;
import com.vkpapps.demo.dtos.auth.UsernamePasswordAuthRequestDto;
import com.vkpapps.demo.dtos.auth.VerifyOtpRequestDto;
import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.otp.OtpService;
import com.vkpapps.demo.services.user.UserService;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  UserService userService;

  @Autowired
  OtpService otpService;

  @Test
  void testSuccessLoginWithUsernameAndPassword() {
    UsernamePasswordAuthRequestDto requestDto = new UsernamePasswordAuthRequestDto();
    requestDto.setUsername("vijaypatidar");
    requestDto.setPassword("12345678");
    User user = getAdminUser();
    userService.saveUser(user).block();
    webClient.post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.token").isNotEmpty()
        .jsonPath("$.refreshToken").isEqualTo(null);

  }

  @Test
  void testFailedLoginWithUsernameAndPassword() {
    UsernamePasswordAuthRequestDto requestDto = new UsernamePasswordAuthRequestDto();
    requestDto.setUsername("vijaypatidar");
    requestDto.setPassword("12345677");//invalid password

    User user = getAdminUser();
    userService.saveUser(user).block();

    webClient.post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.success").isEqualTo(false)
        .jsonPath("$.messages.[0]").isEqualTo("Invalid Credentials");

  }

  @Test
  void testRequestOtpForLogin() {
    OtpRequestDto requestDto = new OtpRequestDto();
    requestDto.setUsername("vijaypatidar");

    User user = getAdminUser();
    userService.saveUser(user).block();

    webClient.post()
        .uri("/auth/send-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.otpRequestId").isNotEmpty();

  }

  @Test
  void testVerifyOtpSuccess() {
    OtpRequestDto requestDto = new OtpRequestDto();
    requestDto.setUsername("vijaypatidar");

    User user = getAdminUser();
    userService.saveUser(user).block();
    Otp otp = otpService.sendOtp(user).block();
    Assertions.assertNotNull(otp);
    VerifyOtpRequestDto verifyOtpRequestDto = new VerifyOtpRequestDto();
    verifyOtpRequestDto.setOtp(otp.getOtpPin());
    verifyOtpRequestDto.setOtpRequestId(otp.getId());

    webClient.post()
        .uri("/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(verifyOtpRequestDto))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.token").isNotEmpty();
  }

  @Test
  void verifyOtpAndLoginFailed() {
    User user = getAdminUser();
    userService.saveUser(user).block();
    Otp otp = otpService.sendOtp(user).block();
    Assertions.assertNotNull(otp);
    VerifyOtpRequestDto verifyOtpRequestDto = new VerifyOtpRequestDto();
    verifyOtpRequestDto.setOtp(1234);
    verifyOtpRequestDto.setOtpRequestId(otp.getId());

    webClient.post()
        .uri("/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(verifyOtpRequestDto))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.success").isEqualTo(false)
        .jsonPath("$.messages.[0]").isEqualTo("Invalid otp.");
  }

  private Otp getOtp(String username) {
    return Otp.builder()
        .id(UUID.randomUUID().toString())
        .otpPin(123456)
        .username(username)
        .validUpTo(new Date(new Date().getTime() + 10 * 60 * 10000))
        .build();
  }

  protected User getAdminUser() {
    return new User("vijaypatidar",
        "12345678",
        "vijay@example.com",
        List.of("ROLE_ADMIN"),
        false
    );
  }

  protected User getNormalUser() {
    return new User("vijaypatidar",
        "12345678",
        "vijay@example.com",
        List.of("ROLE_USER"),
        false
    );
  }
}
