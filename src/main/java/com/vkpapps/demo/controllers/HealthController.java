package com.vkpapps.demo.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController extends AbstractController {
    @RequestMapping
    public Mono<Health> check() {
        log.info("Health check");
        return Mono.just(new Health("cool-webflux"));
    }

    @Data
    @AllArgsConstructor
    static class Health {
        private String serverId;
    }
}
