package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.VariableTransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariableTransactionRepository extends CrudRepository<VariableTransactionEntity, Long> {
}
