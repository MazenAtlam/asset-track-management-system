package com.assettrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.assettrack.domain.Asset;
import com.assettrack.domain.ConditionReport;
import com.assettrack.domain.User;
import com.assettrack.dto.ConditionReportListItemDTO;
import com.assettrack.dto.ConditionReportRequestDTO;

/**
 * Bi-directional mapping for Condition Reports.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConditionReportMapper {

    /**
     * Maps a ConditionReport entity to its list DTO.
     * Uses custom mapping methods to extract and concatenate the asset and reporter names.
     */
    @Mapping(target = "assetId", source = "asset.id")
    @Mapping(target = "assetName", source = "asset")
    @Mapping(target = "reporterName", source = "reporter")
    ConditionReportListItemDTO toDto(ConditionReport report);

    /**
     * Maps an incoming request DTO to a ConditionReport entity.
     * Context-specific fields (id, asset, reporter, status, submittedAt) are ignored
     * because they are resolved and set by the ConditionReportService.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asset", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    ConditionReport toEntity(ConditionReportRequestDTO dto);

    /**
     * Custom mapping from Asset entity to its display name.
     */
    default String mapAssetToName(Asset asset) {
        if (asset == null) return null;
        return asset.getBrand() + " " + asset.getModel();
    }

    /**
     * Custom mapping from User entity to their full name.
     */
    default String mapUserToName(User user) {
        if (user == null) return null;
        return user.getFirstName() + " " + user.getLastName();
    }
}
