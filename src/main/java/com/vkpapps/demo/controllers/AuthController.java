package com.vkpapps.demo.controllers;

import com.vkpapps.demo.dtos.auth.*;
import com.vkpapps.demo.security.jwt.JwtTokenProvider;
import com.vkpapps.demo.services.otp.OtpService;
import com.vkpapps.demo.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController extends AbstractController{
    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserService userService;
    private final OtpService otpService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDto>> login(@Valid @RequestBody Mono<UsernamePasswordAuthRequestDto> authRequest) {

        return authRequest.flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                        .map(this.tokenProvider::createToken)
                )
                .map(this::prepareTokenResponse);
    }

    @PostMapping("/send-otp")
    public Mono<OtpResponseDto> requestOtpForLogin(@Valid @RequestBody OtpRequestDto otpRequestDto) {
        log.info("Username:"+otpRequestDto.getUsername()+" requested for otp.");
        return userService
                .getUsername(otpRequestDto.getUsername())
                .flatMap(otpService::sendOtp)
                .flatMap(otp -> Mono.just(new OtpResponseDto(otp.getId())));
    }

    @PostMapping("/verify-otp")
    public Mono<ResponseEntity<AuthResponseDto>> verifyOtpAndLogin(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
        log.info("Verifying otp for OtpRequestId:"+verifyOtpRequestDto.getOtpRequestId()+".");
        return otpService.verifyOtp(verifyOtpRequestDto.getOtpRequestId(), verifyOtpRequestDto.getOtp())
                .flatMap(otp -> userService.getUsername(otp.getUsername()))
                .flatMap(user -> Mono.just(prepareTokenResponse(tokenProvider.createToken(user))));
    }

    private ResponseEntity<AuthResponseDto> prepareTokenResponse(String jwt) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        return new ResponseEntity<>(new AuthResponseDto(
                jwt,null
        ), httpHeaders, HttpStatus.OK);
    }

}