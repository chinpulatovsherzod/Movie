package com.example.movieapi.exceptions;

public class EmptyFileException extends Throwable {
    public EmptyFileException(String message) {
        super(message);
    }
}