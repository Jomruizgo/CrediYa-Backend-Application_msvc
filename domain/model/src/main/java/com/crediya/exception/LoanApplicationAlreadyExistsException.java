package com.crediya.exception;

import com.crediya.util.Constant;

public class LoanApplicationAlreadyExistsException extends RuntimeException {
    
    public LoanApplicationAlreadyExistsException(String message) {
        super(message);
    }
}