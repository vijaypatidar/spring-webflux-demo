package com.vkpapps.demo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Configuration
@Profile("test")
public class AWSTestConfig {

    @Value("${aws.region:ap-south-1}")
    private String region;

    @Bean
    public SnsAsyncClient snsClient(AwsCredentialsProvider awsCredentialsProvider,LocalStackContainer localStackContainer) {
        return SnsAsyncClient.builder()
                .region(Region.of(region))
                .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.SNS))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
    @Bean
    public AwsCredentialsProvider awsCredentialProvider(LocalStackContainer localStack) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(localStack.getAccessKey(),localStack.getSecretKey()));
    }


}
