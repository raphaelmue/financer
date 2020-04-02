package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.CategoryEntity;
import org.financer.server.domain.model.transaction.FixedTransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixedTransactionRepositoryCustom {

    /**
     * Returns the active fixed transaction that belongs to this id. There should be only one active fixed transaction
     * per category.
     *
     * @param categoryEntity category entity
     * @return fixed transaction object if found
     */
    Optional<FixedTransactionEntity> findActiveTransactionByCategory(CategoryEntity categoryEntity);

}
