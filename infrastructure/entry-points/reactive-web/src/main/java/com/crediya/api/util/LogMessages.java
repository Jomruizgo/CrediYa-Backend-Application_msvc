package com.crediya.api.util;

public final class LogMessages {

    private LogMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Loan Application Handler Messages
    public static final String LOAN_APPLICATION_REGISTER_STARTED = "[CREDIYA-{}] Starting loan application registration";
    public static final String LOAN_APPLICATION_REGISTER_SUCCESS = "[CREDIYA-{}] Loan application registered successfully with ID: {}";
    public static final String LOAN_APPLICATION_REGISTER_ERROR = "[CREDIYA-{}] Error registering loan application";
    public static final String LOAN_APPLICATION_REGISTER_DATA_RECEIVED = "[CREDIYA-{}] Data received for loan application registration. Document: {}";
    
    public static final String LOAN_APPLICATION_SEARCH_BY_ID_STARTED = "[CREDIYA-{}] Starting loan application search by ID: {}";
    public static final String LOAN_APPLICATION_SEARCH_BY_ID_SUCCESS = "[CREDIYA-{}] Loan application found by ID: {}";
    public static final String LOAN_APPLICATION_SEARCH_BY_ID_ERROR = "[CREDIYA-{}] Error searching loan application by ID: {}";
    
    // Loan Type Handler Messages
    public static final String LOAN_TYPE_GET_ALL_STARTED = "[CREDIYA-{}] Starting get all active loan types";
    public static final String LOAN_TYPE_GET_ALL_SUCCESS = "[CREDIYA-{}] Found {} active loan types";
    public static final String LOAN_TYPE_GET_ALL_ERROR = "[CREDIYA-{}] Error getting all active loan types";
    
    public static final String LOAN_TYPE_GET_BY_ID_STARTED = "[CREDIYA-{}] Starting get loan type by ID: {}";
    public static final String LOAN_TYPE_GET_BY_ID_SUCCESS = "[CREDIYA-{}] Loan type found by ID: {}";
    public static final String LOAN_TYPE_GET_BY_ID_ERROR = "[CREDIYA-{}] Error getting loan type by ID: {}";
    
    // Loan Application Review Handler Messages
    public static final String LOAN_APPLICATION_REVIEW_STARTED = "[CREDIYA-{}] Starting loan applications review search";
    public static final String LOAN_APPLICATION_REVIEW_SUCCESS = "[CREDIYA-{}] Successfully retrieved {} loan applications for review";
    public static final String LOAN_APPLICATION_REVIEW_ERROR = "[CREDIYA-{}] Error retrieving loan applications for review";
    
    // Correlation ID messages
    public static final String CORRELATION_ID_PROCESSING = "Processing request with correlation ID: {}";
    public static final String CORRELATION_ID_GENERATED = "Generated new correlation ID: {}";
    public static final String CORRELATION_ID_FROM_HEADER = "Using correlation ID from header: {}";
}