package com.crediya.auth.dto;

public record UpdateUserDocumentRequestDto(
    String name,
    String lastName,
    String documentId,
    java.time.LocalDate birthDate,
    String address,
    String phoneNumber,
    String email,
    java.math.BigDecimal baseSalary,
    String role
) {}