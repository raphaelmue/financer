package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long>, TokenRepositoryCustom {
}
