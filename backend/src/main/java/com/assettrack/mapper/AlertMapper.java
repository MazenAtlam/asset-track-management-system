package com.assettrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.assettrack.domain.Alert;
import com.assettrack.dto.AlertDTO;

/**
 * Mapping for system alerts.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertMapper {

    /**
     * Maps an Alert entity to its DTO representation.
     */
    @Mapping(target = "alertId", source = "id")
    AlertDTO toDto(Alert alert);
}
