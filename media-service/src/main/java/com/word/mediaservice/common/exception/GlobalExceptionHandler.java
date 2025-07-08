package com.word.mediaservice.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleValidationException(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        return Mono.just(ResponseEntity.badRequest().body(errors));
    }

    // NEW: Handle enum parse / JSON type mismatches
    @ExceptionHandler(DecodingException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleDecodingException(DecodingException ex) {
        Throwable cause = ex.getCause();
        String message = "Invalid input format.";
        if (cause instanceof InvalidFormatException formatException) {
            message = formatException.getOriginalMessage();
        } else if (cause instanceof MismatchedInputException mismatchEx) {
            message = mismatchEx.getOriginalMessage(); // more descriptive message
        }
        return Mono.just(ResponseEntity
                .badRequest()
                .body(Map.of("message", message)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(Map.of("message", ex.getMessage())));
    }
}
