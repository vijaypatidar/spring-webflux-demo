package com.vkpapps.demo.services.notification;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

class SnsNotificationServiceTest {

  final Notification notification = Notification.builder()
      .body("Test body")
      .emails(List.of("test@example.com"))
      .build();
  private SnsNotificationService notificationService;
  private ObjectMapper objectMapper;
  private SnsAsyncClient asyncClient;

  @BeforeEach
  void setUp() {
    objectMapper = spy(new ObjectMapper());
    asyncClient = mock(SnsAsyncClient.class);

    notificationService = new SnsNotificationService(
        asyncClient,
        objectMapper
    );
  }

  @Test
  @SneakyThrows
  void sendNotification_Success() {

    final CreateTopicResponse createTopicResponse = mock(CreateTopicResponse.class);
    when(createTopicResponse.topicArn()).thenReturn("mock.snsTopic");
    when(asyncClient.createTopic(any(CreateTopicRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(createTopicResponse));
    when(asyncClient.publish(any(PublishRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(mock(PublishResponse.class)));
    StepVerifier.create(notificationService.sendNotification(notification))
        .verifyComplete();
    verify(asyncClient).createTopic(any(CreateTopicRequest.class));
    verify(createTopicResponse, times(2)).topicArn();
    verify(objectMapper).writeValueAsString(notification);
    verify(asyncClient).publish(any(PublishRequest.class));
  }

  @Test
  @SneakyThrows
  void sendNotification_SuccessWithoutCallingCreateTopic() {
    final CreateTopicResponse createTopicResponse = mock(CreateTopicResponse.class);
    when(createTopicResponse.topicArn()).thenReturn("mock.snsTopic");
    when(asyncClient.createTopic(any(CreateTopicRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(createTopicResponse));
    when(asyncClient.publish(any(PublishRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(mock(PublishResponse.class)));
    StepVerifier.create(notificationService.sendNotification(notification))
        .verifyComplete();
    StepVerifier.create(notificationService.sendNotification(notification))
        .verifyComplete();
    verify(asyncClient).createTopic(any(CreateTopicRequest.class));
    verify(createTopicResponse, times(2)).topicArn();
    verify(objectMapper, times(2)).writeValueAsString(notification);
    verify(asyncClient, times(2)).publish(any(PublishRequest.class));
  }

  @Test
  @SneakyThrows
  void sendNotification_FailedThrowsJsonProcessingException() {
    final CreateTopicResponse createTopicResponse = mock(CreateTopicResponse.class);
    when(createTopicResponse.topicArn()).thenReturn("mock.snsTopic");
    when(asyncClient.createTopic(any(CreateTopicRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(createTopicResponse));
    when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
    StepVerifier.create(notificationService.sendNotification(notification))
        .verifyError();
    verify(asyncClient).createTopic(any(CreateTopicRequest.class));
    verify(createTopicResponse, times(2)).topicArn();
    verify(objectMapper).writeValueAsString(notification);
    verifyNoMoreInteractions(asyncClient);
  }
}