package com.vkpapps.demo.services.otp;

import com.vkpapps.demo.exceptions.ValidationException;
import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.AbstractMongoService;
import com.vkpapps.demo.services.notification.Notification;
import com.vkpapps.demo.services.notification.NotificationService;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class OtpServiceImpl extends AbstractMongoService implements OtpService {
    @NonNull
    private final NotificationService notificationService;
    @NonNull
    private final ReactiveMongoTemplate mongoTemplate;
    @NonNull
    SecureRandom random = new SecureRandom();

    @Override
    public Mono<Otp> sendOtp(User user) {
        var optRequestId = UUID.randomUUID().toString();
        var otp = Otp.builder()
                .id(optRequestId)
                .username(user.getUsername())
                .validUpTo(new Date(new Date().getTime() + 10 * 60 * 1000))
                .otpPin(random.nextInt(900000) + 100000).build();

        return getMongoTemplate().save(otp).flatMap(otp1 -> {
            String body = otp1.getOtpPin() + " is your your Cool spring OTP. Do not share it with anyone.";
            var notification = Notification.builder()
                    .emails(List.of(user.getEmail()))
                    .body(body)
                    .build();
            return notificationService.sendNotification(notification).then(Mono.just(otp));
        });
    }

    @Override
    public Mono<Otp> verifyOtp(String requestId, int otpPin) {
        return getMongoTemplate().findById(requestId, Otp.class)
                .flatMap(otp -> {
                    long currentTime = new Date().getTime();
                    if (otpPin != otp.getOtpPin()) {
                        return Mono.error(new ValidationException("Invalid otp."));
                    } else if (currentTime > otp.getValidUpTo().getTime()) {
                        return Mono.error(new ValidationException("Otp is expired."));
                    } else {
                        return getMongoTemplate().remove(otp).thenReturn(otp);
                    }
                });
    }

    @Override
    protected @NonNull ReactiveMongoTemplate getMongoTemplate() {
        return this.mongoTemplate;
    }
}
