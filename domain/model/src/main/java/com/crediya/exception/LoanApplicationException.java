package com.crediya.exception;

public class LoanApplicationException extends RuntimeException {
    
    public LoanApplicationException(String message) {
        super(message);
    }
    
    public LoanApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}