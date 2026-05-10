package com.assettrack.repository;

import com.assettrack.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 * Provides standard CRUD operations, Specification execution for dynamic
 * searches,
 * and custom query methods for user accounts.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Retrieves a user by their email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user account already exists with the given email.
     */
    boolean existsByEmail(String email);
}