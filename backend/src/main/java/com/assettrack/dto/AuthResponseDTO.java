package com.assettrack.dto;

import com.assettrack.domain.Role;

/**
 * DTO returned to the client upon successful authentication.
 * Contains the JWT token and basic user details.
 */
public class AuthResponseDTO {

    private String token;
    private UserInfo user;

    public AuthResponseDTO(String token, Long id, String email, Role role) {
        this.token = token;
        this.user = new UserInfo(id, email, role);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    /**
     * Nested static class representing safe, non-sensitive user profile data.
     */
    public static class UserInfo {
        private Long id;
        private String email;
        private Role role;

        public UserInfo(Long id, String email, Role role) {
            this.id = id;
            this.email = email;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
    }
}