package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.TokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, Long>, TokenRepositoryCustom {
}
