package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends UserRepositoryCustom, CrudRepository<UserEntity, Long> {
}
