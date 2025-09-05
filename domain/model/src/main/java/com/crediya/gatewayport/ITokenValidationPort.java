package com.crediya.gatewayport;

public interface ITokenValidationPort {
    boolean validateToken(String token);
    String extractUserId(String token);
    String extractRole(String token);
    boolean isTokenExpired(String token);
}