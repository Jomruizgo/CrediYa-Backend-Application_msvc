package com.crediya.r2dbc.util;

public final class LogMessages {
    
    private LogMessages() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Loan Application Persistence Adapter Messages
    public static final String LOAN_APPLICATION_SAVE_STARTED = "[CREDIYA-DB-{}] Starting to save loan application in database";
    public static final String LOAN_APPLICATION_SAVE_SUCCESS = "[CREDIYA-DB-{}] Loan application saved successfully in database with ID: {}";
    public static final String LOAN_APPLICATION_SAVE_ERROR = "[CREDIYA-DB-{}] Error saving loan application in database";
    
    public static final String LOAN_APPLICATION_FIND_BY_ID_STARTED = "[CREDIYA-DB-{}] Starting to find loan application by ID: {}";
    public static final String LOAN_APPLICATION_FIND_BY_ID_SUCCESS = "[CREDIYA-DB-{}] Loan application found in database by ID: {}";
    public static final String LOAN_APPLICATION_FIND_BY_ID_NOT_FOUND = "[CREDIYA-DB-{}] Loan application not found in database by ID: {}";
    public static final String LOAN_APPLICATION_FIND_BY_ID_ERROR = "[CREDIYA-DB-{}] Error finding loan application by ID in database";
    
    public static final String LOAN_APPLICATION_FIND_BY_DOCUMENT_STARTED = "[CREDIYA-DB-{}] Starting to find loan application by document: {} and status: {}";
    public static final String LOAN_APPLICATION_FIND_BY_DOCUMENT_SUCCESS = "[CREDIYA-DB-{}] Loan application found by document: {} and status: {}";
    public static final String LOAN_APPLICATION_FIND_BY_DOCUMENT_NOT_FOUND = "[CREDIYA-DB-{}] No loan application found by document: {} and status: {}";
    public static final String LOAN_APPLICATION_FIND_BY_DOCUMENT_ERROR = "[CREDIYA-DB-{}] Error finding loan application by document and status";
}