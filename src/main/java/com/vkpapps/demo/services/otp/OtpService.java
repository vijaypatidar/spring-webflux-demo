package com.vkpapps.demo.services.otp;

import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import reactor.core.publisher.Mono;

public interface OtpService {
    Mono<Otp> sendOtp(User user);
    Mono<Otp> verifyOtp(String requestId, int otp);
}
