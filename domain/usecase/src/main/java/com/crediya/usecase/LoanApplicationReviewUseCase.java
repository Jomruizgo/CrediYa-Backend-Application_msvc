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
import java.util.Arrays;
import java.util.List;

public class LoanApplicationReviewUseCase implements ILoanApplicationReviewService {

    private static final List<ApplicationStatus> REVIEW_STATUSES = Arrays.asList(
        ApplicationStatus.PENDING_REVIEW,
        ApplicationStatus.REJECTED,
        ApplicationStatus.MANUAL_REVIEW
    );
    

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
        return validateFilter(filter)
                .then(loanApplicationPersistencePort.findApplicationsForReview(filter))
                .flatMap(this::enrichApplicationsPage);
    }
    
    public Mono<Page<LoanApplicationReview>> findApplicationsForReview(String userRole, PageFilter filter) {
        return validateSellerRole(userRole)
                .then(validateFilter(filter))
                .then(loanApplicationPersistencePort.findApplicationsForReview(filter))
                .flatMap(this::enrichApplicationsPage);
    }

    private Mono<Void> validateFilter(PageFilter filter) {
        if (filter == null) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.FILTER_REQUIRED));
        }
        
        if (filter.getPageSize() <= 0 || filter.getPageSize() > Constant.MAX_PAGE_SIZE) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.INVALID_PAGE_SIZE));
        }
        
        if (filter.getPageNumber() < 0) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.INVALID_PAGE_NUMBER));
        }
        
        return Mono.empty();
    }

    private Mono<Void> validateSellerRole(String userRole) {
        if (!UseCaseMessages.SELLER_ROLE.equals(userRole)) {
            return Mono.error(new com.crediya.exception.UnauthorizedUserException(
                String.format(UseCaseMessages.UNAUTHORIZED_SELLER_ROLE, userRole)));
        }
        return Mono.empty();
    }

    public Mono<Page<LoanApplicationReview>> findApplicationsForReviewWithDefaults(
            int pageNumber, 
            int pageSize) {
        
        PageFilter defaultFilter = new PageFilter.Builder()
                .filter("statuses", REVIEW_STATUSES)
                .sortBy(Constant.DEFAULT_SORT_BY)
                .sortOrder(Constant.DEFAULT_SORT_ORDER)
                .page(pageNumber, pageSize > 0 ? pageSize : Constant.DEFAULT_PAGE_SIZE)
                .build();
                
        return findApplicationsForReview(defaultFilter);
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
                calculateApprovedLoansMonthlyPayment(application.getIdentityDocument())
        ).map(tuple -> buildLoanApplicationReview(application, tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private LoanApplicationReview buildLoanApplicationReview(LoanApplication application, 
                                                           UserInfo userInfo, 
                                                           LoanType loanType,
                                                           BigDecimal approvedLoansMonthlyPayment) {
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
                approvedLoansMonthlyPayment,
                application.getCreatedAt()
        );
    }

    private Mono<BigDecimal> calculateApprovedLoansMonthlyPayment(String identityDocument) {
        return loanApplicationPersistencePort.findApprovedApplicationsByIdentityDocument(identityDocument)
                .flatMap(approvedApp -> {
                    return loanTypePersistencePort.findById(approvedApp.getLoan().getLoanTypeId())
                            .map(loanType -> calculateMonthlyPayment(
                                    approvedApp.getLoan().getAmount(),
                                    loanType.getInterestRate(),
                                    approvedApp.getLoan().getTermMonths()
                            ))
                            .onErrorReturn(BigDecimal.ZERO);
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