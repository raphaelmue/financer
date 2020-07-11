package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.VerificationToken;
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
    public Optional<VerificationToken> findByToken(TokenString tokenString) {
        try {
            return Optional.of(entityManager.createQuery("from VerificationToken where token = :token", VerificationToken.class)
                    .setParameter("token", tokenString).getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }
}
