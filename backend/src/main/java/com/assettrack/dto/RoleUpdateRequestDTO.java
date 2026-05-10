package com.assettrack.dto;

import com.assettrack.domain.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for updating a user's role.
 */
public class RoleUpdateRequestDTO {

    @NotNull(message = "Role is required")
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}