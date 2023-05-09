package com.vkpapps.demo.health;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class DeepHealthCheckerTest {

    private DeepHealthChecker deepHealthChecker;
    private HealthChecker healthCheckerMock1;
    private HealthChecker healthCheckerMock2;

    static Stream<Arguments> getFailureCases() {
        return Stream.of(
                Arguments.of(true, false),
                Arguments.of(false, true),
                Arguments.of(false, false)
        );
    }

    @BeforeEach
    void setUp() {
        healthCheckerMock1 = mock(HealthChecker.class);
        healthCheckerMock2 = mock(HealthChecker.class);
        deepHealthChecker = new DeepHealthChecker(Set.of(healthCheckerMock1, healthCheckerMock2));
    }

    @Test
    void isHealthy_success() {
        when(healthCheckerMock1.isHealthy()).thenReturn(Mono.just(true));
        when(healthCheckerMock2.isHealthy()).thenReturn(Mono.just(true));
        StepVerifier.create(deepHealthChecker.isHealthy())
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
        verify(healthCheckerMock1).isHealthy();
        verify(healthCheckerMock2).isHealthy();
    }

    @ParameterizedTest
    @MethodSource(value = "getFailureCases")
    void isHealthy_failed(Boolean health1, Boolean health2) {
        when(healthCheckerMock1.isHealthy()).thenReturn(Mono.just(health1));
        when(healthCheckerMock2.isHealthy()).thenReturn(Mono.just(health2));
        StepVerifier.create(deepHealthChecker.isHealthy())
                .assertNext(Assertions::assertFalse)
                .verifyComplete();
        verify(healthCheckerMock1).isHealthy();
        verify(healthCheckerMock2).isHealthy();
    }
}