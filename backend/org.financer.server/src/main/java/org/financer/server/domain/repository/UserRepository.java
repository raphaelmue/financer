package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends UserRepositoryCustom, JpaRepository<User, Long> {
}
