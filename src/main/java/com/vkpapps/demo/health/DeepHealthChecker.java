package com.vkpapps.demo.health;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class DeepHealthChecker {
    @NonNull
    private final Set<HealthChecker> healthCheckers;

    public Mono<Boolean> isHealthy() {
        return Mono.zip(healthCheckers
                .stream()
                .map(HealthChecker::isHealthy)
                .collect(Collectors.toList()), objects -> Stream.of(objects)
                .map(Boolean.class::cast)
                .reduce(Boolean::logicalAnd)
                .orElse(false));
    }
}
