package com.vkpapps.demo.services.otp;

import com.vkpapps.demo.AbstractTestData;
import com.vkpapps.demo.models.Otp;
import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.notification.Notification;
import com.vkpapps.demo.services.notification.NotificationService;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class OtpServiceImplTest extends AbstractTestData {

    @Mock
    ReactiveMongoTemplate mockReactiveMongoTemplate;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    OtpServiceImpl otpService;


    @Test
    void testSendOtp() {
        User user = getNormalUser();
        Mockito.when(mockReactiveMongoTemplate.save(Mockito.any(Otp.class)))
                .thenAnswer((Answer<Mono<Otp>>) invocation -> Mono.just(invocation.getArgument(0, Otp.class)));
        Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(Mono.empty());
        Mono<Otp> otpMono = otpService.sendOtp(user);
        StepVerifier.create(otpMono)
                .assertNext(Assertions::assertNotNull).verifyComplete();
        Mockito.verify(mockReactiveMongoTemplate).save(Mockito.any(Otp.class));
        Mockito.verify(notificationService).sendNotification(Mockito.any(Notification.class));
    }

    @Test
    void testVerifyOtpSuccess() {
        Otp otp = Otp.builder()
                .id(UUID.randomUUID().toString())
                .username("vijaypatidar")
                .otpPin(123456)
                .validUpTo(new Date(new Date().getTime() + 10 * 60 * 1000))
                .build();
        Mockito.when(mockReactiveMongoTemplate.findById(otp.getId(), Otp.class)).thenReturn(Mono.just(otp));
        Mockito.when(mockReactiveMongoTemplate.remove(otp)).thenReturn(Mono.empty());
        Mono<Otp> otpMono = otpService.verifyOtp(otp.getId(), 123456);
        StepVerifier.create(otpMono)
                .assertNext(Assertions::assertNotNull).verifyComplete();
        Mockito.verify(mockReactiveMongoTemplate).findById(Mockito.eq(otp.getId()), Mockito.any());
        Mockito.verify(mockReactiveMongoTemplate).remove(Mockito.any(Otp.class));
    }
    @Test
    void testVerifyOtpFailsDueToInvalidOtp() {
        Otp otp = Otp.builder()
                .id(UUID.randomUUID().toString())
                .username("vijaypatidar")
                .otpPin(123456)
                .validUpTo(new Date(new Date().getTime() + 10 * 60 * 1000))
                .build();
        Mockito.when(mockReactiveMongoTemplate.findById(otp.getId(), Otp.class)).thenReturn(Mono.just(otp));
        Mockito.when(mockReactiveMongoTemplate.remove(otp)).thenReturn(Mono.empty());
        Mono<Otp> otpMono = otpService.verifyOtp(otp.getId(), 123123);
        StepVerifier.create(otpMono)
                .expectError().verify();
        Mockito.verify(mockReactiveMongoTemplate).findById(Mockito.eq(otp.getId()), Mockito.any());
        Mockito.verify(mockReactiveMongoTemplate,Mockito.times(0)).remove(Mockito.any(Otp.class));
    }
    @Test
    void testVerifyOtpFailsDueToOtpExpired() {
        Otp otp = Otp.builder()
                .id(UUID.randomUUID().toString())
                .username("vijaypatidar")
                .otpPin(123456)
                .validUpTo(new Date(new Date().getTime()))
                .build();
        Mockito.when(mockReactiveMongoTemplate.findById(otp.getId(), Otp.class)).thenReturn(Mono.just(otp));
        Mockito.when(mockReactiveMongoTemplate.remove(otp)).thenReturn(Mono.empty());
        Mono<Otp> otpMono = otpService.verifyOtp(otp.getId(), 123456);
        StepVerifier.create(otpMono)
                .expectError().verify();
        Mockito.verify(mockReactiveMongoTemplate).findById(Mockito.eq(otp.getId()), Mockito.any());
        Mockito.verify(mockReactiveMongoTemplate,Mockito.times(0)).remove(Mockito.any(Otp.class));
    }
}