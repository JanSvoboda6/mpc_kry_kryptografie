package com.web.security.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository providing access to {@link Role}.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    Optional<Role> findByName(RoleType name);
}
