package com.assettrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.assettrack.domain.ConditionReport;

/**
 * Spring Data JPA repository for {@link ConditionReport} entities.
 *
 * <p>Extends {@link JpaSpecificationExecutor} to support the dynamic
 * filtering (by status, issueType, assetId) needed by the admin
 * "List Reports" endpoint without hard-coding query permutations.
 */
public interface ConditionReportRepository
        extends JpaRepository<ConditionReport, Long>, JpaSpecificationExecutor<ConditionReport> {
}
