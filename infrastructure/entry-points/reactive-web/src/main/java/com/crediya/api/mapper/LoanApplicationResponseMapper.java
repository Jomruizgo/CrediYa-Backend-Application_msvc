package com.crediya.api.mapper;

import com.crediya.api.dto.response.LoanApplicationResponseDto;
import com.crediya.model.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationResponseMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "identityDocument", source = "identityDocument")
    @Mapping(target = "amount", source = "loan.amount")
    @Mapping(target = "termMonths", source = "loan.termMonths")
    @Mapping(target = "loanType", source = "loan.type")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    LoanApplicationResponseDto toResponseDto(LoanApplication loanApplication);
}