package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class FixedTransactionRepositoryCustomImpl implements FixedTransactionRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Optional<FixedTransaction> findActiveTransactionByCategory(Category category) {
        try {
            return Optional.of(entityManager.createQuery("from FixedTransaction where category.id = :categoryId and end_date is null", FixedTransaction.class)
                    .setParameter("categoryId", category.getId())
                    .getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }
}
