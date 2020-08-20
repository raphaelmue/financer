package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.FixedTransactionAmount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedTransactionAmountRepository extends CrudRepository<FixedTransactionAmount, Long> {
}
