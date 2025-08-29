package com.crediya.auth.util;

public class AuthErrorMessages {
    
    public static final String DOCUMENT_ID_MISMATCH = "The provided document ID does not match the one registered for this user";
    public static final String USER_NOT_FOUND = "User with ID %s not found in auth service";
    public static final String AUTH_SERVICE_ERROR = "Error communicating with auth service";
    
    private AuthErrorMessages() {
        // Utility class
    }
}