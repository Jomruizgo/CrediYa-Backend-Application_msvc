package com.crediya.util;

public class UseCaseMessages {
    
    // Validation messages
    public static final String IDENTITY_DOCUMENT_REQUIRED = "Identity document is required";
    public static final String LOAN_TYPE_REQUIRED = "Loan type is required";
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be greater than zero";
    public static final String TERM_REQUIRED = "Term is required";
    public static final String TERM_MUST_BE_POSITIVE = "Term must be greater than zero";
    
    // Business validation messages
    public static final String INVALID_LOAN_TYPE = "Invalid loan type ID: %s";
    public static final String AMOUNT_BELOW_MINIMUM = "Amount %s is below minimum %s for loan type %s";
    public static final String AMOUNT_EXCEEDS_MAXIMUM = "Amount %s exceeds maximum %s for loan type %s";
    public static final String TERM_BELOW_MINIMUM = "Term %s months is below minimum %s for loan type %s";
    public static final String TERM_EXCEEDS_MAXIMUM = "Term %s months exceeds maximum %s for loan type %s";
    
    // Entity not found messages
    public static final String LOAN_APPLICATION_NOT_FOUND = "Loan application not found with id: %s";
    
    // Business logic messages
    public static final String LOAN_APPLICATION_ALREADY_EXISTS = "Already exists a pending loan application with this identity document.";
    
    // Authorization messages
    public static final String UNAUTHORIZED_USER_ROLE = "Only CLIENT role can apply for loans. Current role: %s";
    public static final String CLIENT_ROLE = "ROLE_CLIENT";
    
    // Filter validation messages
    public static final String FILTER_REQUIRED = "Filter is required";
    public static final String INVALID_PAGE_SIZE = "Page size must be between 1 and 100";
    public static final String INVALID_PAGE_NUMBER = "Page number must be non-negative";
    
    private UseCaseMessages() {
        // Utility class
    }
}