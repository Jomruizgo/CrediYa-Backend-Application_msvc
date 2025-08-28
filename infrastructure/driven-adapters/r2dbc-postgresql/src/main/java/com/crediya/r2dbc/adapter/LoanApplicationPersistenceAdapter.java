package com.crediya.r2dbc.adapter;

import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.model.LoanApplication;
import com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import com.crediya.r2dbc.repository.LoanApplicationR2dbcRepository;
import com.crediya.r2dbc.util.LogMessages;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class LoanApplicationPersistenceAdapter implements ILoanApplicationPersistencePort {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationPersistenceAdapter.class);
    private final LoanApplicationR2dbcRepository repository;
    private final LoanApplicationEntityMapper mapper;

    public LoanApplicationPersistenceAdapter(LoanApplicationR2dbcRepository repository, 
                                           LoanApplicationEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        logger.info(LogMessages.SAVING_LOAN_APPLICATION, loanApplication.getId());
        
        return repository.save(mapper.toEntity(loanApplication))
                .map(mapper::toDomain)
                .doOnSuccess(saved -> 
                    logger.info(LogMessages.LOAN_APPLICATION_SAVED_SUCCESSFULLY, saved.getId()))
                .doOnError(error -> 
                    logger.error(LogMessages.ERROR_SAVING_LOAN_APPLICATION, loanApplication.getId(), error));
    }

    @Override
    public Mono<LoanApplication> findById(String id) {
        logger.info(LogMessages.FINDING_LOAN_APPLICATION_BY_ID, id);
        
        return repository.findById(id)
                .map(mapper::toDomain)
                .doOnSuccess(found -> 
                    logger.info(LogMessages.LOAN_APPLICATION_FOUND, id))
                .doOnError(error -> 
                    logger.error(LogMessages.ERROR_FINDING_LOAN_APPLICATION, id, error));
    }
}