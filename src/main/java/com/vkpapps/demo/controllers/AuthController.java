package com.vkpapps.demo.controllers;

import com.vkpapps.demo.models.auth.AuthResponse;
import com.vkpapps.demo.models.auth.UsernamePasswordAuthRequest;
import com.vkpapps.demo.security.jwt.JwtTokenProvider;
import com.vkpapps.demo.validators.Validator;
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
public class AuthController {
    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final Validator validator;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody Mono<UsernamePasswordAuthRequest> authRequest) {

        return authRequest.flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                        .map(this.tokenProvider::createToken)
                )
                .map(this::prepareTokenResponse);
    }

    private ResponseEntity<AuthResponse> prepareTokenResponse(String jwt) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        return new ResponseEntity<>(new AuthResponse(
                jwt,null
        ), httpHeaders, HttpStatus.OK);
    }

}