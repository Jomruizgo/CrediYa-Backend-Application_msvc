package com.crediya.exception;

import com.crediya.util.Constant;

public class LoanApplicationNotFoundException extends RuntimeException {
    
    public LoanApplicationNotFoundException(String id) {
        super(String.format(Constant.LOAN_APPLICATION_NOT_FOUND_BY_ID, id));
    }
}