package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.Token;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepositoryCustom {

    Optional<Token> getTokenByToken(TokenString tokenString);

    Optional<Token> getTokenByIPAddress(long userId, IPAddress ipAddress);

}
