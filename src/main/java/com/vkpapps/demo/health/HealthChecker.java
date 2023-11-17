package com.vkpapps.demo.health;

import reactor.core.publisher.Mono;

/**
 * Implement this interface for different external services like database,
 * aws service on which this service depends to function properly.
 */
public interface HealthChecker {
  Mono<Boolean> isHealthy();
}
