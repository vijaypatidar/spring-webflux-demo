package com.vkpapps.demo.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vkpapps.demo.dtos.auth.OtpRequestDto;
import com.vkpapps.demo.dtos.auth.UsernamePasswordAuthRequestDto;
import com.vkpapps.demo.dtos.auth.VerifyOtpRequestDto;
import com.vkpapps.demo.exceptions.ValidationException;
import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.otp.OtpService;
import com.vkpapps.demo.services.user.UserService;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient()
class AuthControllerTest extends AbstractControllerTest {

  @MockBean
  private UserService userService;
  @MockBean
  private OtpService otpService;

  @Test
  void testSuccessLoginWithUsernameAndPassword() {
    UsernamePasswordAuthRequestDto requestDto = new UsernamePasswordAuthRequestDto();
    requestDto.setUsername("vijaypatidar");
    requestDto.setPassword("12345678");

    User user = getAdminUser();

    when(userService.getUsername(requestDto.getUsername())).thenReturn(Mono.just(user));

    webClient.post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.token").isNotEmpty()
        .jsonPath("$.refreshToken").isEmpty();
    verify(userService, Mockito.times(1)).getUsername("vijaypatidar");

  }

  @Test
  void testFailedLoginWithUsernameAndPassword() {
    UsernamePasswordAuthRequestDto requestDto = new UsernamePasswordAuthRequestDto();
    requestDto.setUsername("vijaypatidar");
    requestDto.setPassword("12345677");//invalid password

    User user = getAdminUser();

    when(userService.getUsername(requestDto.getUsername())).thenReturn(Mono.just(user));

    webClient.post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.success").isEqualTo(false)
        .jsonPath("$.messages.[0]").isEqualTo("Invalid Credentials");

    verify(userService, Mockito.times(1)).getUsername("vijaypatidar");
  }

  @Test
  void requestOtpForLogin() {
    OtpRequestDto requestDto = new OtpRequestDto();
    requestDto.setUsername("vijaypatidar");

    User user = getAdminUser();
    Otp otp = getOtp(requestDto.getUsername());

    when(userService.getUsername(requestDto.getUsername())).thenReturn(Mono.just(user));
    when(otpService.sendOtp(user)).thenReturn(Mono.just(otp));

    webClient.post()
        .uri("/auth/send-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.otpRequestId").isEqualTo(otp.getId());

    verify(userService, Mockito.times(1)).getUsername("vijaypatidar");
    verify(otpService, Mockito.times(1)).sendOtp(user);

  }

  @Test
  void verifyOtpAndLoginSuccess() {
    User user = getAdminUser();
    Otp otp = getOtp(user.getUsername());

    VerifyOtpRequestDto requestDto = new VerifyOtpRequestDto();
    requestDto.setOtp(123456);
    requestDto.setOtpRequestId(otp.getId());


    when(otpService.verifyOtp(otp.getId(), 123456)).thenReturn(Mono.just(otp));
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

    webClient.post()
        .uri("/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.token").isNotEmpty();
  }

  @Test
  void verifyOtpAndLoginFailed() {
    User user = getAdminUser();
    Otp otp = getOtp(user.getUsername());

    VerifyOtpRequestDto requestDto = new VerifyOtpRequestDto();
    requestDto.setOtp(123457);
    requestDto.setOtpRequestId(otp.getId());


    when(otpService.verifyOtp(otp.getId(), 123457)).thenReturn(
        Mono.error(new ValidationException("Invalid otp.")));
    when(userService.getUsername(user.getUsername())).thenReturn(Mono.just(user));

    webClient.post()
        .uri("/auth/verify-otp")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestDto))
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
}