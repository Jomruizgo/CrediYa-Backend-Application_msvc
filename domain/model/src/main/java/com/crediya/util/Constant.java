package com.crediya.util;

public class Constant {
    
    // Validation messages
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be greater than zero";
    public static final String TERM_REQUIRED = "Term is required";
    public static final String TERM_MUST_BE_POSITIVE = "Term must be greater than zero";
    public static final String LOAN_TYPE_REQUIRED = "Loan type is required";
    public static final String IDENTITY_DOCUMENT_REQUIRED = "Identity document is required";
    
    // Business messages
    public static final String APPLICATION_REGISTERED_SUCCESSFULLY = "Application registered successfully";
    public static final String ERROR_REGISTERING_APPLICATION = "Error registering application";
    
    // Default states
    public static final String INITIAL_APPLICATION_STATE = "PENDING_REVIEW";
    
    private Constant() {
        // Utility class
    }
}