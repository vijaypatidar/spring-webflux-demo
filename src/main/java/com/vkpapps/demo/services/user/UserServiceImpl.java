package com.vkpapps.demo.services.user;

import com.vkpapps.demo.exceptions.ValidationException;
import com.vkpapps.demo.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    @Override
    public Mono<User> getUsername(String userId) {

        if ("vijaypatidar".equals(userId)){
            User user = new User();
            user.setUserId(userId);
            user.setEmail("vijay@gmail.com");
            user.setPassword(passwordEncoder.encode("12345678"));
            user.setRole("ADMIN");
            return Mono.just(user);
        }
        return Mono.error(new ValidationException("Username "+userId+" not found"));
    }
}
