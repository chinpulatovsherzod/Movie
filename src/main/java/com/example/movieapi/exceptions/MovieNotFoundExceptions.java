package com.example.movieapi.exceptions;

public class MovieNotFoundExceptions extends RuntimeException{

    public MovieNotFoundExceptions(String message){
        super(message);
    }
}
