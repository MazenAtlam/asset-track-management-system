package com.assettrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.assettrack.domain.AllocationHistory;
import com.assettrack.domain.User;
import com.assettrack.dto.AllocationHistoryDTO;

/**
 * Bi-directional mapping for Asset Allocation History.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AllocationMapper {

    /**
     * Maps an AllocationHistory entity to its DTO representation.
     */
    @Mapping(target = "historyId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user")
    AllocationHistoryDTO toDto(AllocationHistory history);

    /**
     * Custom mapping from User entity to their full name.
     */
    default String mapUserToName(User user) {
        if (user == null) return null;
        return user.getFirstName() + " " + user.getLastName();
    }
}
