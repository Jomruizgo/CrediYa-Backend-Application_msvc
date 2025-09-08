package com.crediya.auth.util;

public final class SecurityMessages {
    
    private SecurityMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // JWT Propagation Messages
    public static final String FILTERING_REQUEST_TO = "Filtering request to: {}";
    public static final String AVAILABLE_CONTEXT_KEYS = "Available context keys: {}";
    public static final String PROPAGATING_JWT_TOKEN = "Propagating JWT token to auth microservice for URI: {} with token: {}...";
    public static final String JWT_TOKEN_NOT_FOUND_IN_CONTEXT = "JWT token not found in context for request to: {}";
}