package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.TokenEntity;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenEntityRepositoryCustom {

    Optional<TokenEntity> getTokenByToken(TokenString tokenString);

    Optional<TokenEntity> getTokenByIPAddress(IPAddress ipAddress);

}
