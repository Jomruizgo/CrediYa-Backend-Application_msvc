package com.crediya.serviceport;

import com.crediya.model.LoanApplicationReview;
import com.crediya.model.Page;
import com.crediya.model.PageFilter;
import reactor.core.publisher.Mono;

public interface ILoanApplicationReviewService {
    
    Mono<Page<LoanApplicationReview>> findApplicationsForReview(String userRole, PageFilter filter);
}