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
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidLoanApplicationDataException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidLoanApplicationDataException(InvalidLoanApplicationDataException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error("[CREDIYA-{}] Loan application validation error: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
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
        logger.error("[CREDIYA-{}] Loan application not found: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
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
        logger.error("[CREDIYA-{}] Loan application already exists: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
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
        logger.error("[CREDIYA-{}] Validation error: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            "Validation failed: " + validationErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleConstraintViolationException(ConstraintViolationException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error("[CREDIYA-{}] Constraint violation: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        String constraintErrors = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            "Validation failed: " + constraintErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleWebClientResponseException(WebClientResponseException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error("[CREDIYA-{}] Auth service error ({}): {} at {}", correlationId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex.getStackTrace()[0]);
        
        // Extraer mensaje de error del cuerpo de respuesta si está disponible
        String errorMessage = extractErrorMessage(ex.getResponseBodyAsString());
        if (errorMessage.isEmpty()) {
            errorMessage = "Error communicating with authentication service";
        }
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            errorMessage,
            getErrorTypeFromHttpStatus(ex.getStatusCode()),
            LocalDateTime.now()
        );
        
        // Propagar el mismo código de estado que devolvió auth
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error("[CREDIYA-{}] Validation error: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
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
        logger.error("[CREDIYA-{}] Unexpected error occurred: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.UNEXPECTED_ERROR,
            ErrorConstants.INTERNAL_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private String getCorrelationId(ServerWebExchange exchange) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }
        return correlationId;
    }
    
    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "";
        }
        
        // Intentar extraer mensaje de la respuesta JSON del microservicio auth
        try {
            // Buscar patrón: "message":"texto del error"
            int messageStart = responseBody.indexOf("\"message\":\"");
            if (messageStart != -1) {
                messageStart += 11; // Longitud de "message":"
                int messageEnd = responseBody.indexOf("\"", messageStart);
                if (messageEnd != -1) {
                    return responseBody.substring(messageStart, messageEnd);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse error message from response body: {}", responseBody);
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