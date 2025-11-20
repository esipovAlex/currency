package org.example.exception;

public class DataBaseErrorException extends RuntimeException{

    public DataBaseErrorException(String message) {
        super(message);
    }
}
