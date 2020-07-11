package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryCustom {

    Optional<User> findByEmail(Email email);

}
