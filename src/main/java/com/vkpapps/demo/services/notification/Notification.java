package com.vkpapps.demo.services.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Notification {
    private List<String> emails;
    private String body;
}
