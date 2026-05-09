package com.assettrack.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.assettrack.domain.User;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Provides the email-based lookup required by Spring Security's
 * {@code UserDetailsService}, plus admin-facing paginated list queries.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     * This is the primary lookup used during JWT authentication and login.
     *
     * @param email the email address to search for
     * @return an Optional containing the matching user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Returns a page of users whose first name, last name, or email contains
     * the given search term (case-insensitive). Used by the admin user-list endpoint.
     *
     * @param search   the substring to match against name/email fields
     * @param pageable pagination and sorting parameters
     * @return a page of matching users
     */
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);
}
