package com.vkpapps.demo.health;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DummyHealthChecker implements HealthChecker {
    @Override
    public Mono<Boolean> isHealthy() {
        return Mono.just(true);
    }
}
