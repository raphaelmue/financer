package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.UserEntity;
import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepositoryCustom {

    Optional<UserEntity> findByEmail(Email email);

}
