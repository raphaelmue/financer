package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.CategoryEntity;
import org.financer.server.domain.model.transaction.FixedTransactionEntity;
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
    public Optional<FixedTransactionEntity> findActiveTransactionByCategory(CategoryEntity categoryEntity) {
        try {
            return Optional.of(entityManager.createQuery("from FixedTransactionEntity where category.id = :categoryId and end_date is null", FixedTransactionEntity.class)
                    .setParameter("categoryId", categoryEntity.getId())
                    .getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }
}
