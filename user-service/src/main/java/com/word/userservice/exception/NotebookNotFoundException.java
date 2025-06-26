package com.word.userservice.exception;

public class NotebookNotFoundException extends RuntimeException {
    public NotebookNotFoundException(String message) {
        super(message);
    }
}
