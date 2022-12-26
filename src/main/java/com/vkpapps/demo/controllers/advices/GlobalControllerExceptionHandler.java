package com.vkpapps.demo.controllers.advices;

import com.vkpapps.demo.exceptions.ValidationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @Value("${mode:dev}")
    private String mode;

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException ex) {
        var response = new ErrorResponse();
        response.setMessages(List.of(ex.getMessage()));
        if ("dev".equals(mode)) {
            response.setStackTraces(ex.getStackTrace());
        }
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleValidationExceptions(
            Exception ex) {
        return buildErrorResponse(ex);
    }

    private ErrorResponse buildErrorResponse(Exception ex) {
        List<String> messages = new LinkedList<>();
        var errorResponse = new ErrorResponse();
        errorResponse.setMessages(messages);

        if ("dev".equals(mode)) {
            errorResponse.setStackTraces(ex.getStackTrace());
        }

        if (ex instanceof WebExchangeBindException) {
            Map<String, String> invalidFields = new HashMap<>();
            ((WebExchangeBindException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                invalidFields.put(fieldName, errorMessage);
            });
            errorResponse.setFields(invalidFields);
            messages.add("Please provide valid value for all required field.");
        } else if (ex instanceof ValidationException) {
            messages.addAll(((ValidationException) ex).getMessages());
        } else {
            messages.add(ex.getMessage());
            log.error(ex.getMessage(), ex);
        }
        return errorResponse;
    }

    @Data
    static class ErrorResponse {
        private List<String> messages;
        private StackTraceElement[] stackTraces;
        private Map<String, String> fields;
        private boolean success = false;
    }
}