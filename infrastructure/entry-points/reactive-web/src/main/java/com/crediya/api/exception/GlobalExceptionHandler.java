package com.crediya.api.exception;

import com.crediya.exception.LoanApplicationException;
import com.crediya.errorhandling.ExceptionResponse;
import com.crediya.errorhandling.util.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LoanApplicationException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleLoanApplicationException(LoanApplicationException ex) {
        logger.error("Loan application validation error: {}", ex.getMessage(), ex);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.UNEXPECTED_ERROR,
            ErrorConstants.INTERNAL_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}