package com.vkpapps.demo.controllers.helpers;

import com.vkpapps.demo.dtos.UserDto;
import com.vkpapps.demo.dtos.auth.OtpResponseDto;
import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;

public interface ModelToDtoConverter {
  default Mono<UserDto> toDto(User user) {
    return Mono.just(getModelMapper().map(user, UserDto.class));
  }

  default Mono<OtpResponseDto> toDto(Otp otp) {
    return Mono.just(new OtpResponseDto(otp.getId()));
  }

  ModelMapper getModelMapper();
}
