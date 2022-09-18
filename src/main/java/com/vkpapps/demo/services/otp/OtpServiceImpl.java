package com.vkpapps.demo.services.otp;

import com.vkpapps.demo.exceptions.ValidationException;
import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.AbstractMongoService;
import com.vkpapps.demo.services.notification.Notification;
import com.vkpapps.demo.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class OtpServiceImpl extends AbstractMongoService implements OtpService {
    private final NotificationService notificationService;

    @Override
    public Mono<Otp> sendOtp(User user) {
        String optRequestId = UUID.randomUUID().toString();
        Otp otp = Otp.builder()
                .id(optRequestId)
                .username(user.getUsername())
                .validUpTo(new Date(new Date().getTime() + 10 * 60 * 1000))
                .otp(109040).build();

        return reactiveMongoTemplate.save(otp).flatMap(otp1 -> {
            String body = otp1.getOtp() + " is your your Cool spring OTP. Do not share it with anyone.";
            Notification notification = Notification.builder()
                    .emails(List.of(user.getEmail()))
                    .body(body)
                    .build();
            return notificationService.sendNotification(notification).then(Mono.just(otp));
        });
    }

    @Override
    public Mono<Otp> verifyOtp(String requestId, int otp) {
        return reactiveMongoTemplate.findById(requestId, Otp.class)
                .flatMap(otp1 -> {
                    long currentTime = new Date().getTime();
                    if (otp != otp1.getOtp()) {
                        return Mono.error(new ValidationException("Invalid otp."));
                    } else if (currentTime > otp1.getValidUpTo().getTime()) {
                        return Mono.error(new ValidationException("Otp is expired."));
                    } else {
                        return reactiveMongoTemplate.remove(otp1).thenReturn(otp1);
                    }
                });
    }
}
