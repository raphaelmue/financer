package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.VerificationTokenEntity;
import org.financer.shared.domain.model.value.objects.TokenString;

import java.util.Optional;

public interface VerificationTokenRepositoryCustom {

    Optional<VerificationTokenEntity> findByToken(TokenString tokenString);

}
