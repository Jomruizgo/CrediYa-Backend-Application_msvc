package com.crediya.jwtsecurity.util;

public class JwtMessages {
    
    private JwtMessages() {
        // Utility class
    }
    
    public static final String TOKEN_VALIDATION_FAILED = "Token validation failed";
    public static final String FAILED_TO_EXTRACT_USER_ID = "Failed to extract user ID from token";
    public static final String FAILED_TO_EXTRACT_ROLE = "Failed to extract role from token";
    public static final String FAILED_TO_CHECK_TOKEN_EXPIRATION = "Failed to check token expiration";
}