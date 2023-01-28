package com.vkpapps.demo.configs.aws;

import com.vkpapps.demo.dtos.auth.OtpRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Configuration
@Profile(value = "!test")
public class AWSConfig {

    @Value("${aws.region:ap-south-1}")
    private String region;

    @Bean
    public AwsCredentialsProvider awsCredentialProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public SnsAsyncClient snsClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SnsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

}
