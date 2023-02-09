package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.Token;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    @Query("from Token where token = :tokenString")
    Optional<Token> getTokenByToken(TokenString tokenString);

    @Query("from Token where ipAddress = :ipAddress and user.id = :userId")
    Optional<Token> getTokenByIPAddress(long userId, IPAddress ipAddress);

}
