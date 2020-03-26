package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.VerificationTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationTokenEntity, Long>, VerificationTokenRepositoryCustom {
}
