package com.assettrack.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.Asset;
import com.assettrack.domain.AssetCondition;
import com.assettrack.domain.ConditionReport;
import com.assettrack.domain.IssueType;
import com.assettrack.domain.ReportStatus;
import com.assettrack.domain.Status;
import com.assettrack.domain.User;
import com.assettrack.dto.ConditionReportListItemDTO;
import com.assettrack.dto.ConditionReportRequestDTO;
import com.assettrack.repository.AssetRepository;
import com.assettrack.repository.ConditionReportRepository;
import com.assettrack.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

/**
 * Business logic for §2.3.2 Asset Condition Reporting.
 *
 * <p>Users can submit condition reports against their currently assigned assets.
 * Admins and Managers can list and filter all submitted reports for follow-up.
 */
@Service
@Transactional
public class ConditionReportService {

    private final ConditionReportRepository reportRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public ConditionReportService(ConditionReportRepository reportRepository,
                                  AssetRepository assetRepository,
                                  UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Submit Condition Report  (POST /api/v1/assets/{id}/reports)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Submits a condition report for the given asset.
     *
     * <p>The reporter is resolved from the current Spring Security principal.
     * Only the user currently assigned the asset (or an Admin/Manager) should
     * call this; the controller enforces authentication but does not restrict
     * by assignment — that policy decision is left to the team.
     *
     * @param assetId   the asset being reported on
     * @param dto       the report content
     * @return the ID of the newly created report
     * @throws EntityNotFoundException if the asset or reporter does not exist
     */
    public Long submitReport(Long assetId, ConditionReportRequestDTO dto) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + assetId));

        User reporter = resolveCurrentUser();

        ConditionReport report = new ConditionReport(
                asset,
                reporter,
                IssueType.valueOf(dto.getIssueType().toUpperCase()),
                dto.getDescription(),
                AssetCondition.valueOf(dto.getCondition().toUpperCase()),
                LocalDateTime.now()
        );

        return reportRepository.save(report).getId();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // List Reports  (GET /api/v1/reports)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated list of condition reports with optional filters.
     *
     * @param page      1-based page number
     * @param size      page size
     * @param status    filter by {@link ReportStatus} name (e.g., "PENDING")
     * @param issueType filter by {@link IssueType} name (e.g., "HARDWARE")
     * @param assetId   filter reports for a specific asset ID
     * @return page of report list DTOs
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Transactional(readOnly = true)
    public Page<ConditionReportListItemDTO> listReports(int page, int size,
                                                        String status,
                                                        String issueType,
                                                        Long assetId) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("submittedAt").descending());

        Specification<ConditionReport> spec = buildReportSpec(status, issueType, assetId);
        return reportRepository.findAll(spec, pageable).map(this::toListItemDTO);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Resolves the currently authenticated user from the Security context.
     * The principal name is the user's email (set by {@link com.assettrack.security.CustomUserDetailsService}).
     */
    private User resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Authenticated user not found in database: " + email));
    }

    private Specification<ConditionReport> buildReportSpec(String status,
                                                            String issueType,
                                                            Long assetId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"),
                        ReportStatus.valueOf(status.toUpperCase())));
            }

            if (issueType != null && !issueType.isBlank()) {
                predicates.add(cb.equal(root.get("issueType"),
                        IssueType.valueOf(issueType.toUpperCase())));
            }

            if (assetId != null) {
                predicates.add(cb.equal(root.get("asset").get("id"), assetId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ConditionReportListItemDTO toListItemDTO(ConditionReport report) {
        ConditionReportListItemDTO dto = new ConditionReportListItemDTO();
        dto.setId(report.getId());
        dto.setAssetId(report.getAsset().getId());

        // Build asset display name: "Brand Model"
        Asset asset = report.getAsset();
        dto.setAssetName(asset.getBrand() + " " + asset.getModel());

        User reporter = report.getReporter();
        dto.setReporterName(reporter.getFirstName() + " " + reporter.getLastName());

        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus().name());
        dto.setSubmittedAt(report.getSubmittedAt());
        return dto;
    }
}
