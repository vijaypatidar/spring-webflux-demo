package com.vkpapps.demo.controllers;

import com.vkpapps.demo.dtos.UserDto;
import com.vkpapps.demo.exporters.Exporter;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.ealsticsearch.ElasticSearchService;
import com.vkpapps.demo.services.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Slf4j
public class UserController extends AbstractController {
    private final UserService userService;
    private final ElasticSearchService searchService;

    @GetMapping
    public Mono<UserDto> getCurrentLogged(Principal principal) {
        return userService.getUsername(principal.getName()).flatMap(this::toDto);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserDto> getUserByUsername(@PathVariable String username, Authentication authentication) {
        return userService.getUsername(username).flatMap(this::toDto);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<Map> searchUser(@RequestParam(name = "query") String query, Authentication authentication) {
        return Flux
                .fromIterable(searchService.querySearch(query, List.of("username", "email"), "users"));
    }

    @GetMapping(value = "/export/{format}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> export(ServerWebExchange webExchange, @PathVariable String format) {
        try {
            var exporter = this.applicationContext.getBean(format.toUpperCase() + "_EXPORTER", Exporter.class);
            var dataSource = Exporter.ExportDataSource.builder()
                    .serverWebExchange(webExchange)
                    .headers(Mono.just(List.of("Name", "Email", "Roles")))
                    .rows(userService.getUsers().flatMap(user -> Mono.just(List.of(user.getUsername(), user.getEmail(), String.join("|", user.getRoles())))))
                    .fileName("users")
                    .build();

            exporter.setDataSource(dataSource);
            return exporter.export();
        } catch (NoSuchBeanDefinitionException e) {
            log.error("Invalid export type selected. Format:${}", format);
            return Mono.error(new ValidationException("Format:" + format + " does not supported"));
        }
    }

}
