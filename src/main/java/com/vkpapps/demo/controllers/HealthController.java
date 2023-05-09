package com.vkpapps.demo.controllers;

import com.vkpapps.demo.health.DeepHealthChecker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
@Slf4j
@RequiredArgsConstructor
public class HealthController extends AbstractController {

    @NonNull
    private final DeepHealthChecker deepHealthChecker;

    @RequestMapping
    public Mono<Health> check() {
        log.info("Health check");
        return deepHealthChecker.isHealthy().flatMap(healthy -> {
            if (healthy)
                return Mono.just(new Health("cool-webflux"));
            else return Mono.error(new Exception("Service is not healthy"));
        });
    }

    @Data
    @AllArgsConstructor
    static class Health {
        private String serverId;
    }
}
