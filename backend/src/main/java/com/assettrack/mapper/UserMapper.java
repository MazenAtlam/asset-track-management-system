package com.assettrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.assettrack.domain.User;
import com.assettrack.dto.SignupRequestDTO;
import com.assettrack.dto.UserResponseDTO;

/**
 * Bi-directional mapping between User entities and their corresponding DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Maps a User entity to a UserResponseDTO.
     */
    UserResponseDTO toDto(User user);

    /**
     * Maps a SignupRequestDTO to a User entity.
     * ID, role, and password are ignored here as they are securely handled
     * by the AuthService.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(SignupRequestDTO dto);
}
