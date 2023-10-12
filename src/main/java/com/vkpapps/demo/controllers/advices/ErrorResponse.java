package com.vkpapps.demo.controllers.advices;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ErrorResponse {
  private List<String> messages;
  private StackTraceElement[] stackTraces;
  private Map<String, String> fields;
  private boolean success = false;
}