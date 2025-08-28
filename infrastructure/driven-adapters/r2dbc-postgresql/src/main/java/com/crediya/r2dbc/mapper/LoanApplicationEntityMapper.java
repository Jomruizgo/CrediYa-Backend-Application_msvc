package com.crediya.r2dbc.mapper;

import com.crediya.model.LoanApplication;
import com.crediya.model.Loan;
import com.crediya.model.LoanType;
import com.crediya.model.ApplicationStatus;
import com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LoanApplicationEntityMapper {

    @Mapping(target = "amount", source = "loan.amount")
    @Mapping(target = "termMonths", source = "loan.termMonths")
    @Mapping(target = "loanType", source = "loan.type", qualifiedByName = "loanTypeToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    LoanApplicationEntity toEntity(LoanApplication domain);

    @Mapping(target = "loan", source = ".", qualifiedByName = "entityToLoan")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    LoanApplication toDomain(LoanApplicationEntity entity);

    @Named("loanTypeToString")
    default String loanTypeToString(LoanType loanType) {
        return loanType != null ? loanType.name() : null;
    }

    @Named("statusToString")
    default String statusToString(ApplicationStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default ApplicationStatus stringToStatus(String status) {
        return status != null ? ApplicationStatus.valueOf(status) : null;
    }

    @Named("entityToLoan")
    default Loan entityToLoan(LoanApplicationEntity entity) {
        return new Loan(
                entity.getAmount(),
                entity.getTermMonths(),
                LoanType.valueOf(entity.getLoanType())
        );
    }
}