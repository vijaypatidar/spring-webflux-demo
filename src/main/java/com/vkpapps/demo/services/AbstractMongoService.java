package com.vkpapps.demo.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

public abstract class AbstractMongoService {
    @Getter
    @Setter(onParam = @__({@NonNull}), onMethod = @__({@Autowired}))
    private ReactiveMongoTemplate mongoTemplate;

}
