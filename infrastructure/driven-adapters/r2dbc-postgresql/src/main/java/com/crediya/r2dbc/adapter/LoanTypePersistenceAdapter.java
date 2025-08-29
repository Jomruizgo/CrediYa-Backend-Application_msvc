package com.crediya.r2dbc.adapter;

import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.model.LoanType;
import com.crediya.r2dbc.mapper.LoanTypeEntityMapper;
import com.crediya.r2dbc.repository.LoanTypeR2dbcRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypePersistenceAdapter implements ILoanTypePersistencePort {
    
    private final LoanTypeR2dbcRepository repository;
    private final LoanTypeEntityMapper mapper;
    private final TransactionalOperator transactionalOperator;
    
    public LoanTypePersistenceAdapter(LoanTypeR2dbcRepository repository, 
                                    LoanTypeEntityMapper mapper,
                                    TransactionalOperator transactionalOperator) {
        this.repository = repository;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
    }
    
    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<LoanType> findAllActive() {
        return repository.findAllActive()
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<LoanType> findByIdAndActive(Long id) {
        return repository.findByIdAndActive(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<LoanType> save(LoanType loanType) {
        return repository.save(mapper.toEntity(loanType))
                .map(mapper::toDomain)
                .as(transactionalOperator::transactional);
    }
    
    @Override
    public Mono<LoanType> update(LoanType loanType) {
        return repository.save(mapper.toEntity(loanType))
                .map(mapper::toDomain)
                .as(transactionalOperator::transactional);
    }
    
    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id)
                .as(transactionalOperator::transactional);
    }
}