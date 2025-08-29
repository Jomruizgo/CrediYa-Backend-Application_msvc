package com.crediya.auth.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserDto(
    Long id,
    String name,
    String lastName,
    String documentId,
    LocalDate birthDate,
    String address,
    String phoneNumber,
    String email,
    BigDecimal baseSalary,
    String role
) {}