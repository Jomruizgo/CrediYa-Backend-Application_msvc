package com.crediya.r2dbc.util;

public class LogMessages {
    
    // Persistence log messages
    public static final String SAVING_LOAN_APPLICATION = "Saving loan application with ID: {}";
    public static final String LOAN_APPLICATION_SAVED_SUCCESSFULLY = "Loan application saved successfully with ID: {}";
    public static final String ERROR_SAVING_LOAN_APPLICATION = "Error saving loan application with ID: {}";
    
    public static final String FINDING_LOAN_APPLICATION_BY_ID = "Finding loan application by ID: {}";
    public static final String LOAN_APPLICATION_FOUND = "Loan application found with ID: {}";
    public static final String ERROR_FINDING_LOAN_APPLICATION = "Error finding loan application with ID: {}";
    
    private LogMessages() {
        // Utility class
    }
}