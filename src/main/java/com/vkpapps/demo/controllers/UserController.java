package com.vkpapps.demo.controllers;

import com.vkpapps.demo.dtos.UserDto;
import com.vkpapps.demo.exporters.Exporter;
import com.vkpapps.demo.services.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class UserController extends AbstractController {
    private final UserService userService;

    @GetMapping
    public Mono<UserDto> getCurrentLogged(Principal principal) {
        return userService.getUsername(principal.getName()).flatMap(this::toDto);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserDto> getUserByUsername(@PathVariable String username, Authentication authentication) {
        return userService.getUsername(username).flatMap(this::toDto);
    }

    @GetMapping(value = "/export/{format}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> export(ServerWebExchange webExchange, @PathVariable String format) {
        try {
            Exporter exporter = this.applicationContext.getBean(format.toUpperCase() + "_EXPORTER", Exporter.class);
            exporter.setServerWebExchange(webExchange);
            exporter.setFileName("users");
            exporter.setHeaders(Mono.just(List.of("Name", "Email", "Roles")));
            exporter.setRows(userService.getUsers().flatMap(user -> Mono.just(List.of(user.getUsername(), user.getEmail(), String.join("|", user.getRoles())))));
            return exporter.export();
        } catch (NoSuchBeanDefinitionException e) {
            return Mono.error(new ValidationException("Format:" + format + " does not supported"));
        }
    }

}
