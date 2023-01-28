package com.vkpapps.demo.configs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Profile("test")
public class LocalStackConfig {
    // Any mock-AWS services required for any tests need to be added to this
    // manifest so that LocalStack knows what to spin up.
    private static final LocalStackContainer.Service[] REQUIRED_SERVICES = {
            LocalStackContainer.Service.SNS
    };

    @Bean
    public LocalStackContainer localStackContainer() {
        var localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.11.3"))
                .withServices(
                        REQUIRED_SERVICES
                );
        localStackContainer.start();

        return localStackContainer;
    }
}
