package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Optional;

@Repository
public class FixedTransactionRepositoryCustomImpl implements FixedTransactionRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Optional<FixedTransaction> findActiveTransactionByCategory(Category category) {
        try {
            return Optional.of(entityManager.createQuery("from FixedTransaction where category.id = :categoryId and timeRange.endDate is null", FixedTransaction.class)
                    .setParameter("categoryId", category.getId())
                    .getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FixedTransaction> findAllActiveTransactionsByUserId(long userId) {
        try {
            return entityManager.createQuery("from FixedTransaction where category.user.id = :userId and timeRange.endDate is null", FixedTransaction.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (NoResultException ignored) {
        }
        return Collections.emptyList();
    }
}
