package com.vkpapps.demo.exceptions;

import java.util.LinkedList;
import java.util.List;
import javax.validation.constraints.NotNull;

public class ValidationException extends Exception {
  private final List<String> messages;

  public ValidationException(@NotNull List<String> messages) {
    this.messages = messages;
  }

  public ValidationException(@NotNull String message) {
    this.messages = new LinkedList<>();
    this.messages.add(message);
  }

  public List<String> getMessages() {
    return messages;
  }
}
