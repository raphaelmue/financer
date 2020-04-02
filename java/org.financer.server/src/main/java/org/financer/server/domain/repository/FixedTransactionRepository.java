package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.FixedTransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedTransactionRepository extends CrudRepository<FixedTransactionEntity, Long>, FixedTransactionRepositoryCustom {
}
