package com.assettrack.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.Role;
import com.assettrack.domain.User;
import com.assettrack.dto.UserResponseDTO;
import com.assettrack.exception.ResourceNotFoundException;
import com.assettrack.mapper.UserMapper;
import com.assettrack.repository.UserRepository;
import com.assettrack.repository.UserSpecification;

/**
 * Business logic layer for User Management operations.
 * Handles fetching, filtering, and updating system users.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Returns a paginated and optionally filtered list of users.
     * Maps the User entity to a UserResponseDTO to ensure sensitive information 
     * like passwords never cross the controller boundary.
     *
     * @param pageable The pagination and sorting context.
     * @param search   Optional string to match against email, first name, or last name.
     * @param role     Optional Role to filter by.
     * @return A Page of UserResponseDTO matching the criteria.
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsers(Pageable pageable, String search, Role role) {
        return userRepository.findAll(UserSpecification.filterUsers(search, role), pageable)
                .map(userMapper::toDto);
    }

    /**
     * Updates the role of an existing user.
     * Maps the updated User entity to a UserResponseDTO before returning.
     *
     * @param id      The ID of the user to update.
     * @param newRole The new role to assign.
     * @return The updated User mapped to UserResponseDTO.
     * @throws ResourceNotFoundException if the user doesn't exist.
     */
    public UserResponseDTO updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}