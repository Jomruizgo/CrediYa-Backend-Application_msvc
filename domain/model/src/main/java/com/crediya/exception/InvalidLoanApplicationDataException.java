package com.crediya.exception;

public class InvalidLoanApplicationDataException extends RuntimeException {
    
    public InvalidLoanApplicationDataException(String message) {
        super(message);
    }
}