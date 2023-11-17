package com.vkpapps.demo.services.notification;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
  private List<String> emails;
  private String body;
}
