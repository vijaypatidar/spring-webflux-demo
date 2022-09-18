package com.vkpapps.demo.services.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
public class SnsNotificationService implements NotificationService {
    private final SnsAsyncClient snsAsyncClient;
    private String notificationTopiArn = null;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendNotification(Notification notification) {
        return getNotificationTopiArn().flatMap(arn -> {
            try {
                PublishRequest request = PublishRequest.builder()
                        .message(objectMapper.writeValueAsString(notification))
                        .topicArn(arn)
                        .build();
                return Mono.fromFuture(snsAsyncClient.publish(request)).flatMap(publishResponse -> {

                    return Mono.empty();
                });
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<String> getNotificationTopiArn() {
        if (this.notificationTopiArn == null) {
            CreateTopicRequest request = CreateTopicRequest.builder().name("notifications").build();
            return Mono.fromFuture(snsAsyncClient.createTopic(request))
                    .flatMap(topic -> {
                        SnsNotificationService.this.notificationTopiArn = topic.topicArn();
                        return Mono.just(topic.topicArn());
                    });

        } else {
            return Mono.just(this.notificationTopiArn);
        }
    }
}
