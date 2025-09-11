package com.crediya.usecase;

import com.crediya.model.LoanApplication;
import com.crediya.model.LoanApplicationReview;
import com.crediya.model.Page;
import com.crediya.model.PageFilter;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.UserInfo;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.model.LoanType;
import com.crediya.serviceport.ILoanApplicationReviewService;
import com.crediya.exception.InvalidLoanApplicationDataException;
import com.crediya.util.UseCaseMessages;
import com.crediya.util.Constant;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

public class LoanApplicationReviewUseCase implements ILoanApplicationReviewService {

    private final ILoanApplicationPersistencePort loanApplicationPersistencePort;
    private final IAuthCommunicationPort authCommunicationPort;
    private final ILoanTypePersistencePort loanTypePersistencePort;

    public LoanApplicationReviewUseCase(ILoanApplicationPersistencePort loanApplicationPersistencePort,
                                      IAuthCommunicationPort authCommunicationPort,
                                      ILoanTypePersistencePort loanTypePersistencePort) {
        this.loanApplicationPersistencePort = loanApplicationPersistencePort;
        this.authCommunicationPort = authCommunicationPort;
        this.loanTypePersistencePort = loanTypePersistencePort;
    }

    @Override
    public Mono<Page<LoanApplicationReview>> findApplicationsForReview(PageFilter filter) {
        return Mono.fromCallable(() -> {
                    // Validate filter
                    if (filter == null) {
                        throw new com.crediya.exception.InvalidLoanApplicationDataException(UseCaseMessages.FILTER_REQUIRED);
                    }
                    
                    Integer pageSize = filter.getPageSize();
                    if (pageSize == null || pageSize <= 0 || pageSize > Constant.MAX_PAGE_SIZE) {
                        throw new com.crediya.exception.InvalidLoanApplicationDataException(UseCaseMessages.INVALID_PAGE_SIZE);
                    }
                    
                    Integer pageNumber = filter.getPageNumber();
                    if (pageNumber == null || pageNumber < 0) {
                        throw new com.crediya.exception.InvalidLoanApplicationDataException(UseCaseMessages.INVALID_PAGE_NUMBER);
                    }
                    
                    return filter;
                })
                .flatMap(validatedFilter -> loanApplicationPersistencePort.findApplicationsForReview(validatedFilter))
                .flatMap(this::enrichApplicationsPage);
    }
    

    private Mono<Page<LoanApplicationReview>> enrichApplicationsPage(Page<LoanApplication> applicationPage) {
        return Flux.fromIterable(applicationPage.getContent())
                .flatMap(this::enrichLoanApplication, 10) // Concurrency Control
                .collectList()
                .map(enrichedContent -> new Page<>(
                        enrichedContent,
                        applicationPage.getTotalElements(),
                        applicationPage.getPageNumber(),
                        applicationPage.getPageSize()
                ));
    }

    private Mono<LoanApplicationReview> enrichLoanApplication(LoanApplication application) {
        return Mono.zip(
                authCommunicationPort.getUserInfo(application.getUserId())
                        .onErrorReturn(new UserInfo("N/A", "N/A", BigDecimal.ZERO)),
                loanTypePersistencePort.findById(application.getLoan().getLoanTypeId())
                        .onErrorReturn(createDefaultLoanType()),
                calculateActiveLoansMonthlyPayment(application.getIdentityDocument())
        ).map(tuple -> buildLoanApplicationReview(application, tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private LoanApplicationReview buildLoanApplicationReview(LoanApplication application, 
                                                           UserInfo userInfo, 
                                                           LoanType loanType,
                                                           BigDecimal activeLoansMonthlyPayment) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(
                application.getLoan().getAmount(),
                loanType.getInterestRate(),
                application.getLoan().getTermMonths()
        );

        return new LoanApplicationReview(
                application.getId(),
                application.getIdentityDocument(),
                userInfo.getEmail(),
                userInfo.getName(),
                application.getLoan().getAmount(),
                application.getLoan().getTermMonths(),
                loanType.getName(),
                loanType.getInterestRate(),
                application.getStatus(),
                userInfo.getBaseSalary(),
                monthlyPayment,
                activeLoansMonthlyPayment,
                application.getCreatedAt()
        );
    }

    private Mono<BigDecimal> calculateActiveLoansMonthlyPayment(String identityDocument) {
        if (identityDocument == null || identityDocument.trim().isEmpty()) {
            return Mono.just(BigDecimal.ZERO);
        }
        
        return loanApplicationPersistencePort.findApprovedApplicationsByIdentityDocument(identityDocument)
                .flatMap(activeApp -> {
                    try {
                        if (activeApp == null || activeApp.getLoan() == null) {
                            return Mono.just(BigDecimal.ZERO);
                        }
                        
                        Long loanTypeId = activeApp.getLoan().getLoanTypeId();
                        if (loanTypeId == null) {
                            return Mono.just(BigDecimal.ZERO);
                        }
                        
                        return loanTypePersistencePort.findById(loanTypeId)
                                .map(loanType -> {
                                    try {
                                        return calculateMonthlyPayment(
                                                activeApp.getLoan().getAmount(),
                                                loanType.getInterestRate(),
                                                activeApp.getLoan().getTermMonths()
                                        );
                                    } catch (Exception e) {
                                        return BigDecimal.ZERO;
                                    }
                                })
                                .onErrorReturn(BigDecimal.ZERO);
                    } catch (Exception e) {
                        return Mono.just(BigDecimal.ZERO);
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .onErrorReturn(BigDecimal.ZERO);
    }

    private LoanType createDefaultLoanType() {
        return new LoanType(0L, "Personal Loan", "Default", BigDecimal.ZERO, BigDecimal.ZERO,
                           0, 0, BigDecimal.valueOf(12.5), true, null, null);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal annualRate, Integer termMonths) {
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(termMonths), 2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100 * 12), 10, java.math.RoundingMode.HALF_UP);
        BigDecimal onePlusRate = monthlyRate.add(BigDecimal.ONE);
        BigDecimal onePlusRatePowN = onePlusRate.pow(termMonths);

        return amount
                .multiply(monthlyRate)
                .multiply(onePlusRatePowN)
                .divide(onePlusRatePowN.subtract(BigDecimal.ONE), 2, java.math.RoundingMode.HALF_UP);
    }

}