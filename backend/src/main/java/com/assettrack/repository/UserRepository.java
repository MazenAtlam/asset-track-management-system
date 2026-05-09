package com.assettrack.repository;

import com.assettrack.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 * Provides standard CRUD operations and custom query methods for user accounts.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their email address.
     * Used primarily during authentication to load user credentials.
     *
     * @param email The email to search for.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user account already exists with the given email.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email The email to check.
     * @return true if the email is already in use, false otherwise.
     */
    boolean existsByEmail(String email);
}