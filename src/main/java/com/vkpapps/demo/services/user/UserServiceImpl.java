package com.vkpapps.demo.services.user;

import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.AbstractMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends AbstractMongoService implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final ReactiveValueOperations<String, User> redisTemplate;

    @Override
    public Mono<User> getUsername(String userId) {
        return redisTemplate.get(userId)
                .switchIfEmpty(reactiveMongoTemplate.findOne(Query.query(Criteria.where("username").is(userId)), User.class)
                .flatMap(user -> redisTemplate.set(userId, user).thenReturn(user)));
    }

    @Override
    public Flux<User> getUsers() {
        List<User> users = new LinkedList<>();
        for (int i=0;i<1000;i++){
            users.add(new User("vijay","vijay@email","dad",List.of("role"+i),false));
        }
        return Flux.fromIterable(users);
//        return this.reactiveMongoTemplate.findAll(User.class);
    }
}
