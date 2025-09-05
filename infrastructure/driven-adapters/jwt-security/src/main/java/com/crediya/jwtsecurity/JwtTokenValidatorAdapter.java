package com.crediya.jwtsecurity;

import com.crediya.gatewayport.ITokenValidationPort;
import com.crediya.jwtsecurity.util.JwtMessages;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenValidatorAdapter implements ITokenValidationPort {

    private final SecretKey secretKey;

    public JwtTokenValidatorAdapter(
            @Value("${security.jwt.secret:mySecretKey123456789012345678901234567890}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.debug(JwtMessages.TOKEN_VALIDATION_FAILED, e);
            return false;
        }
    }

    @Override
    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.debug(JwtMessages.FAILED_TO_EXTRACT_USER_ID, e);
            return null;
        }
    }

    @Override
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return (String) claims.get("role");
        } catch (Exception e) {
            log.debug(JwtMessages.FAILED_TO_EXTRACT_ROLE, e);
            return null;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug(JwtMessages.FAILED_TO_CHECK_TOKEN_EXPIRATION, e);
            return true;
        }
    }
}