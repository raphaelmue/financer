package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.VariableTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariableTransactionRepository extends JpaRepository<VariableTransaction, Long>, VariableTransactionRepositoryCustom {

    int PAGE_SIZE = 20;

}
