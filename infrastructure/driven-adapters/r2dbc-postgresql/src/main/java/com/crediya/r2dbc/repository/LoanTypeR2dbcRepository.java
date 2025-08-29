package com.crediya.r2dbc.repository;

import com.crediya.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LoanTypeR2dbcRepository extends R2dbcRepository<LoanTypeEntity, Long> {
    
    @Query("SELECT * FROM loan_types WHERE is_active = true")
    Flux<LoanTypeEntity> findAllActive();
    
    @Query("SELECT * FROM loan_types WHERE id = :id AND is_active = true")
    Mono<LoanTypeEntity> findByIdAndActive(Long id);
    
    @Query("SELECT * FROM loan_types WHERE name = :name AND is_active = true")
    Mono<LoanTypeEntity> findByNameAndActive(String name);
}