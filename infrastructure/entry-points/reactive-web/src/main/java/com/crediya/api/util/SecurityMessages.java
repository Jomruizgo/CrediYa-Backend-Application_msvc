package com.crediya.api.util;

public class SecurityMessages {
    
    private SecurityMessages() {
        // Utility class
    }

    // Token constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ROLE_PREFIX = "ROLE_";
    
    // Log Messages
    public static final String AUTHENTICATION_FAILED_FOR_TOKEN = "Authentication failed for token";
    public static final String INVALID_USER_ID_FORMAT = "Invalid userId format in token: {}";
    public static final String NO_JWT_TOKEN_FOUND = "[CREDIYA-{}] No JWT token found in Authorization header for protected path: {}";
    public static final String INVALID_JWT_TOKEN = "[CREDIYA-{}] Invalid JWT token provided for path: {}";
    public static final String SAVING_TOKEN_IN_CONTEXT = "Saving token in context: {}...";
    

    
}