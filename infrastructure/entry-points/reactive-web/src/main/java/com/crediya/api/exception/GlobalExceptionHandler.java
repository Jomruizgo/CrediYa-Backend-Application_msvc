package com.crediya.api.exception;

import com.crediya.exception.InvalidLoanApplicationDataException;
import com.crediya.exception.LoanApplicationAlreadyExistsException;
import com.crediya.exception.LoanApplicationNotFoundException;
import com.crediya.errorhandling.ExceptionResponse;
import com.crediya.errorhandling.util.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidLoanApplicationDataException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidLoanApplicationDataException(InvalidLoanApplicationDataException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_LOAN_APPLICATION_VALIDATION_ERROR, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(LoanApplicationNotFoundException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleLoanApplicationNotFoundException(LoanApplicationNotFoundException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_LOAN_APPLICATION_NOT_FOUND, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.NOT_FOUND,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(LoanApplicationAlreadyExistsException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleLoanApplicationAlreadyExistsException(LoanApplicationAlreadyExistsException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_LOAN_APPLICATION_ALREADY_EXISTS, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.CONFLICT,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_VALIDATION_ERROR, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.VALIDATION_FAILED_PREFIX + validationErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleConstraintViolationException(ConstraintViolationException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_CONSTRAINT_VIOLATION, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        String constraintErrors = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.VALIDATION_FAILED_PREFIX + constraintErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleWebClientResponseException(WebClientResponseException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_AUTH_SERVICE_ERROR, correlationId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex.getStackTrace()[0]);
        
        String errorMessage = extractErrorMessage(ex.getResponseBodyAsString());
        if (errorMessage.isEmpty()) {
            errorMessage = ErrorConstants.AUTH_SERVICE_ERROR_DEFAULT;
        }
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            errorMessage,
            getErrorTypeFromHttpStatus(ex.getStatusCode()),
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_ILLEGAL_ARGUMENT, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_UNEXPECTED_ERROR, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.UNEXPECTED_ERROR,
            ErrorConstants.INTERNAL_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private String getCorrelationId(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
    }
    
    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "";
        }
        
        try {
            int messageStart = responseBody.indexOf("\"message\":\"");
            if (messageStart != -1) {
                messageStart += 11;
                int messageEnd = responseBody.indexOf("\"", messageStart);
                if (messageEnd != -1) {
                    return responseBody.substring(messageStart, messageEnd);
                }
            }
        } catch (Exception e) {
            logger.warn(ErrorConstants.RESPONSE_BODY_PARSE_WARNING, responseBody);
        }
        
        return "";
    }
    
    private String getErrorTypeFromHttpStatus(HttpStatusCode statusCode) {
        if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
            return ErrorConstants.VALIDATION_ERROR;
        } else if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            return ErrorConstants.NOT_FOUND;
        } else if (statusCode.equals(HttpStatus.CONFLICT)) {
            return ErrorConstants.CONFLICT;
        } else if (statusCode.equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
            return ErrorConstants.VALIDATION_ERROR;
        } else {
            return ErrorConstants.INTERNAL_ERROR;
        }
    }
}