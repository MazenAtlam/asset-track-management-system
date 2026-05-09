package com.assettrack.service;

import com.assettrack.domain.Role;
import com.assettrack.domain.User;
import com.assettrack.dto.AuthResponseDTO;
import com.assettrack.dto.LoginRequestDTO;
import com.assettrack.dto.SignupRequestDTO;
import com.assettrack.repository.UserRepository;
import com.assettrack.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service handling business logic for user authentication and registration.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user into the system.
     *
     * @param request The signup details.
     * @return the ID of the newly created user.
     * @throws IllegalArgumentException if the email is already in use.
     */
    public Long registerUser(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Create new user entity, encoding password immediately
        User newUser = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                Role.DEVELOPER // Default role for new signups
        );

        User savedUser = userRepository.save(newUser);
        return savedUser.getId();
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request The login credentials.
     * @return AuthResponse containing the JWT and user profile data.
     */
    public AuthResponseDTO authenticateUser(LoginRequestDTO request) {
        // This will throw BadCredentialsException if authentication fails
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // Fetch user object to obtain details for the JWT payload and AuthResponse
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found after authentication."));

        // Generate Token
        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponseDTO(jwtToken, user.getId(), user.getEmail(), user.getRole());
    }
}