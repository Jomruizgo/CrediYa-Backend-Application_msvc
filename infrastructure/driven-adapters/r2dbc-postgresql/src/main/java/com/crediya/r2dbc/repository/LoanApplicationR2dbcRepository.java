package com.crediya.r2dbc.repository;

import com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanApplicationR2dbcRepository extends ReactiveCrudRepository<LoanApplicationEntity, String> {
}