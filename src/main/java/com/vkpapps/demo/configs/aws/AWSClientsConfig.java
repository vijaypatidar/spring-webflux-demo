package com.vkpapps.demo.configs.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Configuration
public class AWSClientsConfig {

    @Value("${aws.region:ap-south-1}")
    private String region;
    @Bean
    public SnsAsyncClient snsClient(AwsCredentialsProvider awsCredentialsProvider){
        return SnsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

}