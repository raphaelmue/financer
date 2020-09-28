package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.VariableTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface VariableTransactionRepositoryCustom {

    Page<VariableTransaction> findByCategoryUserId(long userId, Pageable pageable);

}
