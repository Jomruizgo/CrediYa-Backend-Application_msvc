package com.crediya.config;

import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.serviceport.ILoanApplication;
import com.crediya.serviceport.ILoanApplicationReviewService;
import com.crediya.serviceport.ILoanTypeServicePort;
import com.crediya.usecase.LoanApplicationUseCase;
import com.crediya.usecase.LoanApplicationReviewUseCase;
import com.crediya.usecase.LoanTypeUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.crediya.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

}
