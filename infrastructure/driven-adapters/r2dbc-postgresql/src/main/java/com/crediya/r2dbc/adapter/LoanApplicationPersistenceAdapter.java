package com.crediya.r2dbc.adapter;

import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.LoanApplication;
import com.crediya.model.Page;
import com.crediya.model.PageFilter;
import com.crediya.r2dbc.entity.LoanApplicationEntity;
import com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import com.crediya.r2dbc.repository.LoanApplicationR2dbcRepository;
import com.crediya.r2dbc.util.LogMessages;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

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
            logger.info(LogMessages.LOAN_APPLICATION_FIND_BY_DOCUMENT_STARTED, 
                    correlationId, identityDocument, status);
            
            return repository.findByIdentityDocumentAndStatus(identityDocument, status.name())
                    .map(mapper::toDomain)
                    .doOnSuccess(found -> {
                        if (found != null) {
                            logger.info(LogMessages.LOAN_APPLICATION_FIND_BY_DOCUMENT_SUCCESS, 
                                    correlationId, identityDocument, status);
                        } else {
                            logger.info(LogMessages.LOAN_APPLICATION_FIND_BY_DOCUMENT_NOT_FOUND, 
                                    correlationId, identityDocument, status);
                        }
                    })
                    .doOnError(error -> 
                        logger.error(LogMessages.LOAN_APPLICATION_FIND_BY_DOCUMENT_ERROR, correlationId, error));
        });
    }

    @Override
    public Mono<Page<LoanApplication>> findApplicationsForReview(PageFilter filter) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "NO_CONTEXT");
            return findApplicationsWithCorrelation(filter, correlationId);
        });
    }

    private Mono<Page<LoanApplication>> findApplicationsWithCorrelation(PageFilter filter, String correlationId) {
        List<String> statusStrings = extractStatuses(filter);

        return repository.countApplicationsForReview(statusStrings)
                .flatMap(totalElements ->
                        repository.findApplicationsForReview(
                                        statusStrings,
                                        filter.getSortBy(),
                                        filter.getPageSize(),
                                        filter.getOffset()
                                )
                                .map(mapper::toDomain)
                                .collectList()
                                .map(applications -> buildPage(applications, totalElements, filter))
                )
                .doOnSubscribe(sub ->
                        logger.info(LogMessages.LOAN_APPLICATION_REVIEW_SEARCH_STARTED, correlationId, filter))
                .doOnSuccess(page ->
                        logger.info(LogMessages.LOAN_APPLICATION_REVIEW_SEARCH_SUCCESS, correlationId, page.getContent().size()))
                .doOnError(error ->
                        logger.error(LogMessages.LOAN_APPLICATION_REVIEW_SEARCH_ERROR, correlationId, error));
    }

    private List<String> extractStatuses(PageFilter filter) {
        List<ApplicationStatus> statuses = filter.getFilter("statuses", List.class);
        return statuses.stream().map(ApplicationStatus::name).toList();
    }

    @Override
    public Flux<LoanApplication> findApprovedApplicationsByIdentityDocument(String identityDocument) {
        return Flux.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "NO_CONTEXT");
            logger.info("Starting search for approved applications for identity document: {} - CorrelationId: {}", identityDocument, correlationId);
            
            return entityTemplate.select(LoanApplicationEntity.class)
                    .matching(Query.query(
                        Criteria.where("identity_document").is(identityDocument)
                                .and("status").is(ApplicationStatus.APPROVED.name())))
                    .all()
                    .map(mapper::toDomain)
                    .doOnNext(app -> 
                        logger.debug("Found approved application: {} - CorrelationId: {}", app.getId(), correlationId))
                    .doOnComplete(() -> 
                        logger.info("Completed search for approved applications for identity document: {} - CorrelationId: {}", identityDocument, correlationId))
                    .doOnError(error -> 
                        logger.error("Error searching approved applications for identity document: {} - CorrelationId: {} - Error: {}", identityDocument, correlationId, error.getMessage()));
        });
    }

    private Page<LoanApplication> buildPage(List<LoanApplication> applications, Long totalElements, PageFilter filter) {
        return new Page<>(applications, totalElements, filter.getPageNumber(), filter.getPageSize());
    }

}