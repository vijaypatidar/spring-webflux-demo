package com.vkpapps.demo;

import java.util.Map;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = CoolSpringWebfluxApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:3-alpine"))
            .withExposedPorts(6379);
    static GenericContainer<?> mongodb = new GenericContainer<>(DockerImageName.parse("mongo:6.0.1"))
            .withEnv(Map.of(
                    "MONGO_INITDB_ROOT_USERNAME", "root",
                    "MONGO_INITDB_ROOT_PASSWORD", "example"
            ))
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        redis.start();
        mongodb.start();
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.mongodb.uri", () -> String.format("mongodb://root:example@%s:%s", mongodb.getHost(), mongodb.getFirstMappedPort()));
        registry.add("spring.data.mongodb.database", () -> "digital");
    }

    @Autowired
    protected WebTestClient webClient;
}
