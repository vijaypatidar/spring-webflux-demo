package com.vkpapps.demo.services.otp;

import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.user.UserServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {
    @Mock
    ReactiveValueOperations<String, User> mockRedisTemplate;
    @Mock
    PasswordEncoder mockPasswordEncoder;
    @Mock
    ReactiveMongoTemplate mockReactiveMongoTemplate;
    @InjectMocks
    UserServiceImpl userService;


    @Test
    void sendOtp() {
        String userId = "vijaypatidar";
        User user = new User(
                userId,
                "12345678",
                "vijay@example.com",
                List.of(), false
        );
        Mockito.when(mockRedisTemplate.get(userId)).thenReturn(Mono.empty());
        Mockito.when(mockRedisTemplate.set(Mockito.eq(userId), Mockito.any())).thenReturn(Mono.just(true));
        Mockito.when(mockReactiveMongoTemplate.findOne(Mockito.any(), Mockito.any())).thenReturn(Mono.just(user));
        Mono<User> userMono = userService.getUsername(userId);
        StepVerifier.create(userMono)
                .assertNext(actualUser -> {
                    Assertions.assertNotNull(actualUser);
                    Assertions.assertEquals(userId, actualUser.getUsername());
                }).verifyComplete();
        Mockito.verify(mockRedisTemplate).get(userId);
        Mockito.verify(mockRedisTemplate).set(userId, user);

    }

}