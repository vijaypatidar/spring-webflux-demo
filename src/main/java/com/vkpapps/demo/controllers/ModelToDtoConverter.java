package com.vkpapps.demo.controllers;

import com.vkpapps.demo.dtos.UserDto;
import com.vkpapps.demo.models.User;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;

public interface ModelToDtoConverter {
    default Mono<UserDto> toDto(User user) {
        return Mono.just(getModelMapper().map(user, UserDto.class));
    }

    ModelMapper getModelMapper();
}
