package com.crediya.exception;

import com.crediya.util.Constant;

public class LoanApplicationAlreadyExistsException extends RuntimeException {
    
    public LoanApplicationAlreadyExistsException(String identityDocument) {
        super(String.format(Constant.LOAN_APPLICATION_ALREADY_EXISTS, identityDocument));
    }
}