package com.crediya.auth.util;

public class AuthApiPaths {
    
    // Base URL configuration
    public static final String DEFAULT_AUTH_SERVICE_URL = "http://localhost:8081";
    
    // API Paths
    public static final String GET_USER_BY_ID = "/api/v1/user/{id}";
    
    private AuthApiPaths() {
        // Utility class
    }
}