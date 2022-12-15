package com.vkpapps.demo.services;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

public abstract class AbstractMongoService {
    protected abstract ReactiveMongoTemplate getMongoTemplate();
}
