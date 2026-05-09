package com.assettrack.controller;

import com.assettrack.dto.AuthResponseDTO;
import com.assettrack.dto.LoginRequestDTO;
import com.assettrack.dto.SignupRequestDTO;
import com.assettrack.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller exposing authentication endpoints for registration and login.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to register a new user account.
     * * @param request The user's registration details.
     * 
     * @return 201 Created on success, 400 Bad Request if validation fails.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDTO request) {
        try {
            Long userId = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User registered successfully.",
                            "userId", userId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Endpoint to authenticate and generate a JWT for a user.
     *
     * @param request The login credentials.
     * @return 200 OK with AuthResponse on success, 401 Unauthorized if invalid
     *         credentials.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password."));
        }
    }
}