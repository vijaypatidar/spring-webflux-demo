package com.vkpapps.demo.services.user;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vkpapps.demo.models.User;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class UserServiceImplTest {
  @Mock
  ReactiveValueOperations<String, User> mockRedisTemplate;
  @Spy
  PasswordEncoder spyPasswordEncoder;
  @Mock
  ReactiveMongoTemplate mockReactiveMongoTemplate;
  @InjectMocks
  UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    userService.setMongoTemplate(mockReactiveMongoTemplate);
  }

  @Test
  void testGetUsername() {
    String userId = "vijaypatidar";
    User user = new User(
        userId,
        "12345678",
        "vijay@example.com",
        List.of(), false
    );
    when(mockRedisTemplate.get(userId)).thenReturn(Mono.empty());
    when(mockRedisTemplate.set(Mockito.eq(userId), Mockito.any())).thenReturn(Mono.just(true));
    when(mockReactiveMongoTemplate.findOne(Mockito.any(), Mockito.any())).thenReturn(
        Mono.just(user));
    Mono<User> userMono = userService.getUsername(userId);
    StepVerifier.create(userMono)
        .assertNext(actualUser -> {
          Assertions.assertNotNull(actualUser);
          Assertions.assertEquals(userId, actualUser.getUsername());
        }).verifyComplete();
    verify(mockRedisTemplate).get(userId);
    verify(mockRedisTemplate).set(userId, user);

  }

  @Test
  void testSaveUser_success() {
    String password = "12345678";
    String userId = "test.dummy";
    User user = new User(
        userId,
        password,
        "test@example.com",
        List.of(), false
    );
    when(mockReactiveMongoTemplate.save(user)).thenReturn(Mono.just(user));
    Mono<User> userMono = userService.saveUser(user);
    StepVerifier.create(userMono)
        .assertNext(actualUser -> {
          Assertions.assertNotNull(actualUser);
          Assertions.assertEquals(userId, actualUser.getUsername());
        }).verifyComplete();
    verify(mockReactiveMongoTemplate).save(user);
    verify(spyPasswordEncoder).encode(password);
  }
}