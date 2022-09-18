package com.vkpapps.demo.controllers;

import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class UserController {
    private final UserService userService;
    @GetMapping
    public Mono<User> getCurrentLogged(Principal principal){
        return userService.getUsername(principal.getName());
    }
}
