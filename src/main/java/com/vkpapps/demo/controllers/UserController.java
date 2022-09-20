package com.vkpapps.demo.controllers;

import com.vkpapps.demo.dtos.UserDto;
import com.vkpapps.demo.services.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class UserController extends AbstractController {
    private final UserService userService;
    @GetMapping
    public Mono<UserDto> getCurrentLogged(Principal principal){
        return userService.getUsername(principal.getName()).flatMap(this::toDto);
    }
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserDto> getUserByUsername(@PathVariable String username, Authentication authentication){
        return userService.getUsername(username).flatMap(this::toDto);
    }
}
