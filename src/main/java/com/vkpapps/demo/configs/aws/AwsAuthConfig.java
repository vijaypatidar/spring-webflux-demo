package com.vkpapps.demo.configs.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

@Component
public class AwsAuthConfig {
    @Bean
    public AwsCredentialsProvider awsCredentialProvider() {
        return DefaultCredentialsProvider.create();
    }
}
