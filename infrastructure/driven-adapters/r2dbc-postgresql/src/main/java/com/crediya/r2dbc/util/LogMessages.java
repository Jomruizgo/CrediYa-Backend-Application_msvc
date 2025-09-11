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
    
    public static final String LOAN_APPLICATION_REVIEW_SEARCH_STARTED = "[CREDIYA-DB-{}] Starting to find applications for review with filter: {}";
    public static final String LOAN_APPLICATION_REVIEW_SEARCH_SUCCESS = "[CREDIYA-DB-{}] Found {} applications for review";
    public static final String LOAN_APPLICATION_REVIEW_SEARCH_ERROR = "[CREDIYA-DB-{}] Error finding applications for review";
    public static final String LOAN_APPLICATION_USER_DATA_ENRICHMENT_STARTED = "[CREDIYA-DB-{}] Starting to enrich application {} with user data";
    public static final String LOAN_APPLICATION_USER_DATA_ENRICHMENT_SUCCESS = "[CREDIYA-DB-{}] Successfully enriched application {} with user data";
    public static final String LOAN_APPLICATION_USER_DATA_ENRICHMENT_ERROR = "[CREDIYA-DB-{}] Error enriching application {} with user data, using fallback";
    
    public static final String LOAN_APPLICATION_FIND_ACTIVE_LOANS_STARTED = "[CREDIYA-DB-{}] Starting to find active loans (APPROVED and DISBURSED) for identity document: {}";
    public static final String LOAN_APPLICATION_FIND_ACTIVE_LOANS_SUCCESS = "[CREDIYA-DB-{}] Completed search for active loans for identity document: {}";
    public static final String LOAN_APPLICATION_FIND_ACTIVE_LOANS_ERROR = "[CREDIYA-DB-{}] Error searching active loans for identity document: {} - Error: {}";
    public static final String LOAN_APPLICATION_FIND_ACTIVE_LOAN_FOUND = "[CREDIYA-DB-{}] Found active loan application: {} with status: {}";
}