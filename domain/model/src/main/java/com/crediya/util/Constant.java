package com.crediya.util;

public class Constant {
    
    // Validation messages
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be greater than zero";
    public static final String TERM_REQUIRED = "Term is required";
    public static final String TERM_MUST_BE_POSITIVE = "Term must be greater than zero";
    public static final String LOAN_TYPE_REQUIRED = "Loan type is required";
    public static final String IDENTITY_DOCUMENT_REQUIRED = "Identity document is required";
    public static final String INVALID_LOAN_APPLICATION_DATA = "Invalid loan application data";
    public static final String INVALID_ID = "ID cannot be null";
    
    // Error Messages
    public static final String LOAN_APPLICATION_NOT_FOUND_BY_ID = "Loan application with ID %s not found";
    public static final String LOAN_APPLICATION_ALREADY_EXISTS = "Loan application with identity document %s already exists";
    
    // Business messages
    public static final String APPLICATION_REGISTERED_SUCCESSFULLY = "Application registered successfully";
    public static final String ERROR_REGISTERING_APPLICATION = "Error registering application";
    
    // Default states
    public static final String INITIAL_APPLICATION_STATE = "PENDING_REVIEW";
    
    // DTO Validation Messages (Entry Point Layer)
    public static final String DTO_IDENTITY_DOCUMENT_REQUIRED = "Identity document field is mandatory";
    public static final String DTO_AMOUNT_REQUIRED = "Amount field is mandatory";
    public static final String DTO_AMOUNT_MIN = "Amount must be greater than zero";
    public static final String DTO_TERM_REQUIRED = "Term months field is mandatory";
    public static final String DTO_TERM_MIN = "Term months must be greater than zero";
    public static final String DTO_LOAN_TYPE_REQUIRED = "Loan type field is mandatory";
    
    private Constant() {
        // Utility class
    }
}