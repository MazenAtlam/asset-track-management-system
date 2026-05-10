package com.assettrack.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.assettrack.domain.Role;
import com.assettrack.domain.User;

import jakarta.persistence.criteria.Predicate;

/**
 * Utility class to generate Spring Data JPA Specifications for {@link User}.
 * Provides dynamic search functionality on names/emails and role filters.
 */
public class UserSpecification {

    /**
     * Builds a JPA Specification for dynamic user search/filter queries.
     *
     * @param search String to match against email, first name, or last name.
     * @param role   The Role to filter by.
     * @return Specification to be used with UserRepository.
     */
    public static Specification<User> filterUsers(String search, Role role) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern)));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}