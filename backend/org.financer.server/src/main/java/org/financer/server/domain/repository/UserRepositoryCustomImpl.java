package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Optional<User> findByEmail(Email email) {
        try {
            return Optional.of(entityManager.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email).getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }
}
