package com.assettrack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assettrack.domain.Role;
import com.assettrack.domain.User;
import com.assettrack.dto.PaginatedResponseDTO;
import com.assettrack.dto.RoleUpdateRequestDTO;
import com.assettrack.service.UserService;

import jakarta.validation.Valid;

/**
 * REST controller for User Management endpoints.
 * Handles retrieving users and updating authorization roles.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a paginated, filtered list of all users.
     * Access is restricted to ADMIN users.
     * Note: Expects a 1-indexed 'page' query parameter.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role) {

        // Spring Boot uses 0-indexed pages natively, subtract 1 from the request.
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userService.getUsers(pageable, search, role);

        PaginatedResponseDTO<User> response = new PaginatedResponseDTO<>(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                page, // Return the 1-indexed page
                userPage.getContent());

        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing user's system role.
     * Access is restricted to ADMIN users.
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequestDTO request) {

        User updatedUser = userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }
}