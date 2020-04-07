package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixedTransactionRepositoryCustom {

    /**
     * Returns the active fixed transaction that belongs to this id. There should be only one active fixed transaction
     * per category.
     *
     * @param category category entity
     * @return fixed transaction object if found
     */
    Optional<FixedTransaction> findActiveTransactionByCategory(Category category);

}
