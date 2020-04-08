package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;

@Repository
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Iterable<Category> findAllByUserId(long userId) {
        try {
            return entityManager.createQuery("from Category where user.id = :userId and parent is null", Category.class)
                    .setParameter("userId", userId).getResultList();
        } catch (NoResultException ignored) {
        }

        return Collections.emptySet();
    }
}
