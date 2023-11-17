package com.vkpapps.demo.services.notification;

import reactor.core.publisher.Mono;

public interface NotificationService {
  Mono<Void> sendNotification(Notification notification);
}
