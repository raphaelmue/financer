package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.VerificationToken;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    @Query("from VerificationToken where token = :tokenString")
    Optional<VerificationToken> findByToken(TokenString tokenString);

}
