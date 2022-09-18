package com.vkpapps.demo.configs.mongodb;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MongoDbConfig {
    @Value("${mongodb.connection.db}")
    private String dbName;
    @Value("${mongodb.connection.url}")
    private String connectionString;

    @Bean
    public MongoDatabase buildClient() {
        ConnectionString connString = new ConnectionString(
                connectionString
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient.getDatabase(dbName);
    }

}
