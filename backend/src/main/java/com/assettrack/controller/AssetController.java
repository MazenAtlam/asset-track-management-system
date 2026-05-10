package com.assettrack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assettrack.domain.Asset;
import com.assettrack.dto.AssetRequestDTO;
import com.assettrack.dto.AssetUpdateRequest;
import com.assettrack.dto.PaginatedResponseDTO;
import com.assettrack.service.AssetService;

import jakarta.validation.Valid;

/**
 * REST controller for Phase 4 Core Asset Management endpoints.
 * *
 * <p>
 * Handles mapping HTTP requests to the underlying AssetService for CRUD
 * operations,
 * implementing appropriate pagination translation (1-indexed API to 0-indexed
 * Spring Data),
 * and applying method-level security protocols.
 */
@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * Creates a new asset in the system.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Asset> createAsset(@Valid @RequestBody AssetRequestDTO request) {
        Asset newAsset = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAsset);
    }

    /**
     * Returns a paginated, sorted, and filtered list of assets.
     * Available to all authenticated users.
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<Asset>> getAssets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand) {

        // Construct 0-indexed Pageable object for Spring Data
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Asset> assetPage = assetService.getAssets(pageable, search, status, type, brand);

        // Wrap the response in our custom PaginatedResponse using 1-indexed numbering
        PaginatedResponseDTO<Asset> response = new PaginatedResponseDTO<>(
                assetPage.getTotalElements(),
                assetPage.getTotalPages(),
                page,
                assetPage.getContent());

        return ResponseEntity.ok(response);
    }

    /**
     * Gets a single asset by its ID.
     * Available to all authenticated users.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long id) {
        Asset asset = assetService.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    /**
     * Updates an existing asset fully.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable Long id,
            @RequestBody AssetUpdateRequest request) {
        Asset updatedAsset = assetService.updateAsset(id, request);
        return ResponseEntity.ok(updatedAsset);
    }

    /**
     * Permanently deletes an asset.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to quickly find a spare laptop.
     * Available to all authenticated users.
     */
    @GetMapping("/actions/spare-laptop")
    public ResponseEntity<Asset> findSpareLaptop() {
        Asset spareLaptop = assetService.findSpareLaptop();
        return ResponseEntity.ok(spareLaptop);
    }
}