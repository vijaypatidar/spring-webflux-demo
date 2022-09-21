package com.vkpapps.demo.services.user;

import com.vkpapps.demo.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    Mono<User> getUsername(String userId);

    Flux<User> getUsers();
}
