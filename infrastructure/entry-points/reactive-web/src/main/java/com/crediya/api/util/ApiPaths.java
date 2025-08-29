package com.crediya.api.util;

public final class ApiPaths {
    
    public static final String API_V1_BASE = "/api/v1";
    
    // Loan Application paths
    public static final String LOAN_APPLICATION_BASE = API_V1_BASE + "/application";
    public static final String LOAN_APPLICATION_BY_ID = LOAN_APPLICATION_BASE + "/{id}";
    
    // Loan Type paths
    public static final String LOAN_TYPE_BASE = API_V1_BASE + "/loan-types";
    public static final String LOAN_TYPE_BY_ID = LOAN_TYPE_BASE + "/{id}";
    
    private ApiPaths() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}