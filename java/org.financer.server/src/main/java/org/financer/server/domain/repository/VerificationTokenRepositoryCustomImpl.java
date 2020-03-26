package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.VerificationTokenEntity;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public class VerificationTokenRepositoryCustomImpl implements VerificationTokenRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Optional<VerificationTokenEntity> findByToken(TokenString tokenString) {
        try {
            return Optional.of(entityManager.createQuery("from VerificationTokenEntity where token = :token", VerificationTokenEntity.class)
                    .setParameter("token", tokenString).getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }
}
