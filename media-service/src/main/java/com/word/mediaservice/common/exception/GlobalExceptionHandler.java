package com.word.mediaservice.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.word.mediaservice.engagement.exception.DuplicateLikeException;
import com.word.mediaservice.engagement.exception.LikeNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;
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

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors));
    }

    // Handle enum parse / JSON type mismatches
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
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage())));
    }
    // Handles invalid query/path params (e.g., wrong type for `page`)
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleServerWebInput(ServerWebInputException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Invalid query or path parameter: " + ex.getReason())));
    }

    //Generic internal WebFlux/server exceptions
    @ExceptionHandler(ServerErrorException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleServerError(ServerErrorException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Server error: " + ex.getMessage())));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleDuplicateKey(DuplicateKeyException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(Map.of("message", ex.getMessage())));
    }

    @ExceptionHandler(DuplicateLikeException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleDuplicateLike(DuplicateLikeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage())));
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleLikeNotFound(LikeNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage())));
    }
}
