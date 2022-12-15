package com.vkpapps.demo.configs.redis;

import com.vkpapps.demo.models.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, User> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        return buildTemplate(factory, User.class);
    }

    @Bean
    public ReactiveValueOperations<String, User> userReactiveValueOperations(
            ReactiveRedisTemplate<String, User> template) {
        return template.opsForValue();
    }

    private <T> ReactiveRedisTemplate<String, T> buildTemplate(ReactiveRedisConnectionFactory factory, Class<T> tClass) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> valueSerializer = new Jackson2JsonRedisSerializer<>(tClass);
        RedisSerializationContext.RedisSerializationContextBuilder<String, T> builder = RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, T> context = builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
