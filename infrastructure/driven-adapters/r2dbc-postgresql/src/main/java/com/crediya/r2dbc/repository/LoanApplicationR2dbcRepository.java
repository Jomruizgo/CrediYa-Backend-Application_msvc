package com.crediya.r2dbc.repository;

import com.crediya.r2dbc.dto.LoanApplicationJoinResult;
import com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationR2dbcRepository extends ReactiveCrudRepository<LoanApplicationEntity, String> {
    
    Mono<LoanApplicationEntity> findByIdentityDocumentAndStatus(String identityDocument, String status);
    
    @Query("""
        SELECT la.id, la.user_id, la.identity_document, la.loan_type_id, 
               la.amount, la.term_months, la.status, la.created_at
        FROM loan_applications la
        WHERE la.status IN (:statuses)
        ORDER BY 
            CASE WHEN :sortBy = 'createdAt' THEN la.created_at END,
            CASE WHEN :sortBy = 'amount' THEN la.amount END,
            CASE WHEN :sortBy = 'status' THEN la.status END
        LIMIT :pageSize OFFSET :offset
        """)
    Flux<LoanApplicationEntity> findApplicationsForReview(
        @Param("statuses") List<String> statuses,
        @Param("sortBy") String sortBy,
        @Param("pageSize") int pageSize,
        @Param("offset") long offset
    );
    
    @Query("""
        SELECT COUNT(*)
        FROM loan_applications la
        WHERE la.status IN (:statuses)
        """)
    Mono<Long> countApplicationsForReview(@Param("statuses") List<String> statuses);
}