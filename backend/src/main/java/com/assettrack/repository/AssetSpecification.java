package com.assettrack.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.assettrack.domain.Asset;
import com.assettrack.domain.Status;

import jakarta.persistence.criteria.Predicate;

/**
 * Utility class to generate Spring Data JPA Specifications for {@link Asset}.
 * Handles dynamic, multi-field filtering for global search and specific fields.
 */
public class AssetSpecification {

    /**
     * Builds a JPA Specification for dynamic asset search/filter queries.
     * All provided filters are combined using AND logic.
     * * @param search Global search string matching brand, model, or serial number.
     * 
     * @param status The exact status (e.g., AVAILABLE).
     * @param type   The asset type (case-insensitive).
     * @param brand  The asset brand (case-insensitive).
     * @return Specification to be used with AssetRepository.
     */
    public static Specification<Asset> filterAssets(String search, String status, String type, String brand) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("brand")), pattern),
                        cb.like(cb.lower(root.get("model")), pattern),
                        cb.like(cb.lower(root.get("serialNumber")), pattern)));
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("status"), Status.valueOf(status.toUpperCase())));
                } catch (IllegalArgumentException e) {
                    // Ignore malformed status strings, or handle as needed
                }
            }

            if (type != null && !type.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("type")), type.toLowerCase()));
            }

            if (brand != null && !brand.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}