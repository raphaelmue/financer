package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.TokenEntity;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class TokenEntityRepositoryCustomImpl implements TokenEntityRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Optional<TokenEntity> getTokenByToken(TokenString tokenString) {
        try {
            return Optional.of(entityManager.createQuery("from TokenEntity where token = :token", TokenEntity.class)
                    .setParameter("token", tokenString).getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenEntity> getTokenByIPAddress(IPAddress ipAddress) {
        try {
            return Optional.of(entityManager.createQuery("from TokenEntity where ipAddress = :ipAddress", TokenEntity.class)
                    .setParameter("ipAddress", ipAddress).getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }
}
