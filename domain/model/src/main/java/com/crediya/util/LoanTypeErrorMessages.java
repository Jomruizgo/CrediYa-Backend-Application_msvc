package com.crediya.util;

public final class LoanTypeErrorMessages {
    
    public static final String LOAN_TYPE_NOT_FOUND = "Loan type not found with id: %d";
    public static final String LOAN_TYPE_NOT_FOUND_OR_INACTIVE = "Loan type not found or inactive with id: %d";
    public static final String MIN_AMOUNT_GREATER_THAN_MAX = "Minimum amount cannot be greater than maximum amount";
    public static final String MIN_TERM_GREATER_THAN_MAX = "Minimum term cannot be greater than maximum term";
    public static final String INTEREST_RATE_MUST_BE_POSITIVE = "Interest rate must be positive";
    
    private LoanTypeErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}