package org.example.exception;

public class CodeCurrencyAbsentException extends RuntimeException {

    public CodeCurrencyAbsentException(String message) {
        super(message);
    }
}
