package com.assettrack.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assettrack.dto.AllocationHistoryDTO;
import com.assettrack.dto.AllocationRequestDTO;
import com.assettrack.dto.AssetDetailDTO;
import com.assettrack.dto.AssetDTO;
import com.assettrack.dto.AssetListItemDTO;
import com.assettrack.dto.AssetUpdateDTO;
import com.assettrack.dto.ConditionReportRequestDTO;
import com.assettrack.dto.ReturnRequestDTO;
import com.assettrack.dto.SpareLaptopDTO;
import com.assettrack.service.AssetService;
import com.assettrack.service.ConditionReportService;

import jakarta.validation.Valid;

/**
 * REST controller for §2.2 Asset Management endpoints.
 *
 * <p>All routes are under {@code /api/v1/assets}. Role-based access control
 * is enforced via {@code @PreAuthorize} annotations in the service layer.
 *
 * <p><strong>Important:</strong> {@code GET /assets/actions/spare-laptop} is
 * mapped <em>before</em> {@code GET /assets/{id}} so that Spring does not
 * incorrectly treat "actions" as an asset ID.
 */
@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;
    private final ConditionReportService conditionReportService;

    public AssetController(AssetService assetService,
                           ConditionReportService conditionReportService) {
        this.assetService = assetService;
        this.conditionReportService = conditionReportService;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /api/v1/assets — Register Asset (Admin Only)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Adds a new hardware asset to the system.
     *
     * @param dto the asset registration payload (validated)
     * @return 201 Created with {"message": "...", "assetId": ...}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerAsset(@Valid @RequestBody AssetDTO dto) {
        Long assetId = assetService.registerAsset(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Asset created successfully",
                "assetId", assetId
        ));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/assets — Search & List Assets
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated, filtered, sorted list of assets.
     *
     * @param page      1-based page number (default 1)
     * @param size      records per page (default 10)
     * @param sortBy    field to sort by (default "id")
     * @param direction sort direction: "asc" or "desc" (default "desc")
     * @param search    global text search across type, brand, model, serialNumber
     * @param status    filter by asset status
     * @param type      filter by asset type
     * @param brand     filter by brand
     * @return 200 OK with paged asset list
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAssets(
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand) {

        Page<AssetListItemDTO> result =
                assetService.listAssets(page, size, sortBy, direction, search, status, type, brand);

        return ResponseEntity.ok(Map.of(
                "totalItems",  result.getTotalElements(),
                "totalPages",  result.getTotalPages(),
                "currentPage", page,
                "assets",      result.getContent()
        ));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/assets/actions/spare-laptop — Find Spare Laptop
    // NOTE: mapped before /{id} to prevent "actions" being treated as an ID
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns the first available spare laptop with its last known owner.
     *
     * @return 200 OK with spare laptop DTO
     */
    @GetMapping("/actions/spare-laptop")
    public ResponseEntity<SpareLaptopDTO> findSpareLaptop() {
        return ResponseEntity.ok(assetService.findSpareLaptop());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/assets/{id} — Get Asset Details
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns detailed information for a specific asset.
     *
     * @param id the asset ID
     * @return 200 OK with asset detail DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetDetailDTO> getAssetDetails(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetDetails(id));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PUT /api/v1/assets/{id} — Update Asset Details / Status (Admin/Manager)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Partially updates an asset's status or descriptive fields.
     *
     * @param id  the asset ID to update
     * @param dto the update payload (all fields optional)
     * @return 200 OK with {"message": "...", "assetId": ..., "status": "..."}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAsset(
            @PathVariable Long id,
            @RequestBody AssetUpdateDTO dto) {

        String newStatus = assetService.updateAsset(id, dto);
        return ResponseEntity.ok(Map.of(
                "message",  "Asset updated successfully",
                "assetId",  id,
                "status",   newStatus
        ));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // DELETE /api/v1/assets/{id} — Delete Asset (Admin Only)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Permanently removes an asset.
     * Returns 409 Conflict if the asset is currently assigned.
     *
     * @param id the asset ID to delete
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /api/v1/assets/{id}/allocate — Allocate Asset (Admin/Manager)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Assigns an asset to a team member.
     *
     * @param id  the asset ID
     * @param dto the allocation request (userId + allocationDate)
     * @return 200 OK with {"message": "Asset allocated successfully"}
     */
    @PostMapping("/{id}/allocate")
    public ResponseEntity<Map<String, String>> allocateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AllocationRequestDTO dto) {

        assetService.allocateAsset(id, dto);
        return ResponseEntity.ok(Map.of("message", "Asset allocated successfully"));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /api/v1/assets/{id}/return — Return Asset (Admin/Manager)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns an asset from a user back to the IT pool.
     *
     * @param id  the asset ID
     * @param dto the return request (returnDate, conditionUponReturn, newStatus)
     * @return 200 OK with return confirmation
     */
    @PostMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> returnAsset(
            @PathVariable Long id,
            @Valid @RequestBody ReturnRequestDTO dto) {

        Long previousOwnerId = assetService.returnAsset(id, dto);
        String newStatus = dto.getNewStatus() != null ? dto.getNewStatus().toUpperCase() : "AVAILABLE";

        return ResponseEntity.ok(Map.of(
                "message",         "Asset returned successfully",
                "assetId",         id,
                "previousOwnerId", previousOwnerId,
                "currentStatus",   newStatus
        ));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/assets/{id}/history — Get Asset Allocation History
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns the paginated chain of custody for an asset.
     *
     * @param id   the asset ID
     * @param page 1-based page number (default 1)
     * @param size records per page (default 10)
     * @return 200 OK with paged history list
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<Map<String, Object>> getAssetHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<AllocationHistoryDTO> result = assetService.getAssetHistory(id, page, size);
        return ResponseEntity.ok(Map.of(
                "totalItems",  result.getTotalElements(),
                "totalPages",  result.getTotalPages(),
                "currentPage", page,
                "history",     result.getContent()
        ));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /api/v1/assets/{id}/reports — Submit Condition Report
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Allows the currently authenticated user to report an issue with an asset.
     *
     * @param id  the asset ID being reported on
     * @param dto the condition report content
     * @return 201 Created with {"message": "...", "reportId": ...}
     */
    @PostMapping("/{id}/reports")
    public ResponseEntity<Map<String, Object>> submitConditionReport(
            @PathVariable Long id,
            @Valid @RequestBody ConditionReportRequestDTO dto) {

        Long reportId = conditionReportService.submitReport(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message",  "Report submitted successfully",
                "reportId", reportId
        ));
    }
}
