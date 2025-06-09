package com.word.wordservice.exception;

public class InvalidWordException extends RuntimeException{
    public InvalidWordException(String word) {
        super("Invalid word: '" + word + "'");
    }
}
