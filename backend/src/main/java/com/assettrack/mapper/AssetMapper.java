package com.assettrack.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.AfterMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.assettrack.domain.Asset;
import com.assettrack.domain.User;
import com.assettrack.dto.AssetDetailDTO;
import com.assettrack.dto.AssetListItemDTO;
import com.assettrack.dto.AssetRequestDTO;
import com.assettrack.dto.AssetUpdateRequest;

/**
 * Bi-directional mapping between Asset entities and their various DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetMapper {

    /**
     * Maps an Asset to an AssetListItemDTO.
     * The nested assignedUser mapping is handled automatically by the custom mapUser methods below.
     */
    AssetListItemDTO toListItemDto(Asset asset);

    /**
     * Maps an Asset to an AssetDetailDTO.
     */
    AssetDetailDTO toDetailDto(Asset asset);

    /**
     * Maps an AssetRequestDTO to an Asset entity for creation.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    Asset toEntity(AssetRequestDTO dto);

    /**
     * Updates an existing Asset entity from a partial AssetUpdateRequest.
     * Null fields in the request will be ignored and will not overwrite existing entity values.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AssetUpdateRequest dto, @MappingTarget Asset asset);

    /**
     * Custom mapping from User entity to the nested AssignedUserDTO for the list item.
     * Concatenates firstName and lastName.
     */
    default AssetListItemDTO.AssignedUserDTO mapUserToListItemAssignedUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return new AssetListItemDTO.AssignedUserDTO(user.getId(), user.getFirstName() + " " + user.getLastName());
    }

    /**
     * Custom mapping from User entity to the nested AssignedUserDTO for the detail view.
     * Concatenates firstName and lastName.
     */
    default AssetDetailDTO.AssignedUserDTO mapUserToDetailAssignedUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return new AssetDetailDTO.AssignedUserDTO(user.getId(), user.getFirstName() + " " + user.getLastName());
    }

    /**
     * Post-mapping step that fulfills the dynamic expiration tracking requirement
     * without risking stale database state. It sets the 'expired' flag and
     * dynamically suggests actions based on the asset's type.
     */
    @AfterMapping
    default void trackExpiration(Asset asset, @MappingTarget AssetDetailDTO dto) {
        if (asset.getWarrantyExpirationDate() != null && asset.getWarrantyExpirationDate().isBefore(LocalDate.now())) {
            dto.setExpired(true);
            List<String> actions = new ArrayList<>();
            if ("LAPTOP".equalsIgnoreCase(asset.getType())) {
                actions.add("Reassign as SPARE");
                actions.add("DECOMMISSION");
            } else {
                actions.add("DECOMMISSION");
            }
            dto.setSuggestedActions(actions);
        } else {
            dto.setExpired(false);
            dto.setSuggestedActions(new ArrayList<>());
        }
    }
}
