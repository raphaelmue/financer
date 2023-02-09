package org.financer.server.domain.repository;

import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("from User where email = :email")
    Optional<User> findByEmail(Email email);

}
