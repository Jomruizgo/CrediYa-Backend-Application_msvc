package com.crediya.config;

import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.usecase.LoanApplicationUseCase;
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

    @Bean
    public LoanApplicationUseCase loanApplicationUseCase(ILoanApplicationPersistencePort loanApplicationPersistencePort,
                                                       IAuthCommunicationPort authCommunicationPort,
                                                       ILoanTypePersistencePort loanTypePersistencePort) {
        return new LoanApplicationUseCase(loanApplicationPersistencePort, authCommunicationPort, loanTypePersistencePort);
    }

    @Bean
    public LoanTypeUseCase loanTypeUseCase(ILoanTypePersistencePort loanTypePersistencePort) {
        return new LoanTypeUseCase(loanTypePersistencePort);
    }
}
