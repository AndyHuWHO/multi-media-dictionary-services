package com.word.userservice.exception;

public class WordNoteNotFoundException extends RuntimeException {
    public WordNoteNotFoundException(String message) {
        super(message);
    }
}
