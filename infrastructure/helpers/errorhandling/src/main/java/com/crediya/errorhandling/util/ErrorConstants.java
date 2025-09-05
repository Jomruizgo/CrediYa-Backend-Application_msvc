package com.crediya.errorhandling.util;

public class ErrorConstants {
    
    // Error statuses
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String CONFLICT = "CONFLICT";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    
    // Error messages
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";
    
    // Exception handler log messages
    public static final String LOG_LOAN_APPLICATION_VALIDATION_ERROR = "[CREDIYA-{}] Loan application validation error: {} at {}";
    public static final String LOG_LOAN_APPLICATION_NOT_FOUND = "[CREDIYA-{}] Loan application not found: {} at {}";
    public static final String LOG_LOAN_APPLICATION_ALREADY_EXISTS = "[CREDIYA-{}] Loan application already exists: {} at {}";
    public static final String LOG_UNAUTHORIZED_USER = "[CREDIYA-{}] Unauthorized user: {} at {}";
    public static final String LOG_VALIDATION_ERROR = "[CREDIYA-{}] Validation error: {} at {}";
    public static final String LOG_CONSTRAINT_VIOLATION = "[CREDIYA-{}] Constraint violation: {} at {}";
    public static final String LOG_AUTH_SERVICE_ERROR = "[CREDIYA-{}] Auth service error ({}): {} at {}";
    public static final String LOG_ILLEGAL_ARGUMENT = "[CREDIYA-{}] Validation error: {} at {}";
    public static final String LOG_UNEXPECTED_ERROR = "[CREDIYA-{}] Unexpected error occurred: {} at {}";
    
    // Message prefixes
    public static final String VALIDATION_FAILED_PREFIX = "Validation failed: ";
    public static final String AUTH_SERVICE_ERROR_DEFAULT = "Error communicating with authentication service";
    public static final String RESPONSE_BODY_PARSE_WARNING = "Could not parse error message from response body: {}";
    
    private ErrorConstants() {
        // Utility class
    }
}