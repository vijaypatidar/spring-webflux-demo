package com.vkpapps.demo.services.user;

import com.vkpapps.demo.exceptions.ResourceNotFoundException;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.AbstractMongoService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor()
@Slf4j
public class UserServiceImpl extends AbstractMongoService implements UserService {
    @NotNull
    private final PasswordEncoder passwordEncoder;
    @NotNull
    private final ReactiveValueOperations<String, User> redisTemplate;
    @NotNull
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<User> getUsername(String userId) {
        return redisTemplate.get(userId)
                .switchIfEmpty(getMongoTemplate().findOne(Query.query(Criteria.where("username").is(userId)), User.class)
                        .flatMap(user -> redisTemplate.set(userId, user).thenReturn(user)))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with username:" + userId)));
    }

    @Override
    public Flux<User> getUsers() {
        return this.getMongoTemplate().findAll(User.class);
    }

    @Override
    public Mono<User> saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.getMongoTemplate().save(user);
    }

    @Override
    protected @NonNull ReactiveMongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
