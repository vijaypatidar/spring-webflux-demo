package com.vkpapps.demo.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
public class HealthController {
    @Data @AllArgsConstructor
    static class Health{
        private String serverId;
    }

    @PostMapping
    public Mono<Health> check(){
        return Mono.just(new Health("vijay"));
    }
}
