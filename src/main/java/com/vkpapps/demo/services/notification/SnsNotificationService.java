package com.vkpapps.demo.services.notification;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SnsNotificationService implements NotificationService {
    @Override
    public Mono<Void> sendNotification(Notification notification) {

        return Mono.empty();
    }
}
