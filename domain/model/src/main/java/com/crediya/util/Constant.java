package com.crediya.util;

public class Constant {

    // Error Messages
    public static final String LOAN_APPLICATION_NOT_FOUND_BY_ID = "Loan application with ID %s not found";

    
    // Pagination Constants
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_ORDER = "DESC";
    
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