package com.web.security.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for {@link User} providing.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    Boolean existsByUsername(String username);

    List<User> findAllByIsVerified(boolean verified);
}
