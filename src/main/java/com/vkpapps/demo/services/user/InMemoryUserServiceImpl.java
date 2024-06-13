package com.vkpapps.demo.services.user;

import com.vkpapps.demo.models.User;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class InMemoryUserServiceImpl implements UserService {

    final Map<String, User> userMap = new HashMap<>();
    final RateLimiter userServiceRateLimiter;

    InMemoryUserServiceImpl(RateLimiterRegistry rateLimiterRegistry) {
        userMap.put("vijay", new User(
                "vijay",
                "$2a$10$YMMq7Mn7uTQ79AGCqVUWcuMVudi62l1bKIcgBuxE/J.a6Xsi5dnlK",
                "vijay@example.com",
                List.of(
                        "ROLE_ADMIN",
                        "ROLE_USER"
                ), false
        ));
        this.userServiceRateLimiter = rateLimiterRegistry.rateLimiter("userService");
    }

    @Override
    public Mono<User> getUsername(String userId) {
        return Mono.just(this.userMap.get(userId));
    }

    @Override
    public Flux<User> getUsers() {
        return Flux.fromIterable(this.userMap.values())
                .transformDeferred(RateLimiterOperator.of(this.userServiceRateLimiter));
    }

    @Override
    public Mono<User> saveUser(User user) {
        this.userMap.put(user.getUsername(), user);
        return Mono.just(user);
    }
}
