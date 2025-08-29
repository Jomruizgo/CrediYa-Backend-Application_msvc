package com.crediya.r2dbc.adapter;

import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.LoanApplication;
import com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import com.crediya.r2dbc.repository.LoanApplicationR2dbcRepository;
import com.crediya.r2dbc.util.LogMessages;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class LoanApplicationPersistenceAdapter implements ILoanApplicationPersistencePort {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationPersistenceAdapter.class);
    private final LoanApplicationR2dbcRepository repository;
    private final LoanApplicationEntityMapper mapper;
    private final TransactionalOperator transactionalOperator;
    private final R2dbcEntityTemplate entityTemplate;

    public LoanApplicationPersistenceAdapter(LoanApplicationR2dbcRepository repository, 
                                           LoanApplicationEntityMapper mapper,
                                           TransactionalOperator transactionalOperator,
                                           R2dbcEntityTemplate entityTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "NO_CONTEXT");
            logger.info(LogMessages.LOAN_APPLICATION_SAVE_STARTED, correlationId);
            
            return entityTemplate.insert(mapper.toEntity(loanApplication))
                    .map(mapper::toDomain)
                    .doOnSuccess(saved -> 
                        logger.info(LogMessages.LOAN_APPLICATION_SAVE_SUCCESS, correlationId, saved.getId()))
                    .doOnError(error -> 
                        logger.error(LogMessages.LOAN_APPLICATION_SAVE_ERROR, correlationId, error))
                    .as(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<LoanApplication> findById(String id) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "NO_CONTEXT");
            logger.info(LogMessages.LOAN_APPLICATION_FIND_BY_ID_STARTED, correlationId, id);
            
            return repository.findById(id)
                    .map(mapper::toDomain)
                    .doOnSuccess(found -> {
                        if (found != null) {
                            logger.info(LogMessages.LOAN_APPLICATION_FIND_BY_ID_SUCCESS, correlationId, id);
                        } else {
                            logger.info(LogMessages.LOAN_APPLICATION_FIND_BY_ID_NOT_FOUND, correlationId, id);
                        }
                    })
                    .doOnError(error -> 
                        logger.error(LogMessages.LOAN_APPLICATION_FIND_BY_ID_ERROR, correlationId, error));
        });
    }

    @Override
    public Mono<LoanApplication> findByIdentityDocumentAndStatus(String identityDocument, ApplicationStatus status) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "NO_CONTEXT");
            logger.info("[CREDIYA-DB-{}] Starting to find loan application by document: {} and status: {}", 
                    correlationId, identityDocument, status);
            
            return repository.findByIdentityDocumentAndStatus(identityDocument, status.name())
                    .map(mapper::toDomain)
                    .doOnSuccess(found -> {
                        if (found != null) {
                            logger.info("[CREDIYA-DB-{}] Loan application found by document: {} and status: {}", 
                                    correlationId, identityDocument, status);
                        } else {
                            logger.info("[CREDIYA-DB-{}] No loan application found by document: {} and status: {}", 
                                    correlationId, identityDocument, status);
                        }
                    })
                    .doOnError(error -> 
                        logger.error("[CREDIYA-DB-{}] Error finding loan application by document and status", correlationId, error));
        });
    }
}