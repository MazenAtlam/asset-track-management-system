package com.assettrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.assettrack.security.CustomUserDetailsService;

/**
 * Application-wide Spring beans not tied to the security filter chain itself.
 *
 * <p>Separated from {@link SecurityConfig} to keep security filter configuration
 * clean and to avoid circular-dependency issues between the authentication provider
 * and the security configuration.
 */
@Configuration
public class AppConfig {

    private final CustomUserDetailsService userDetailsService;

    public AppConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * BCrypt password encoder with the default cost factor (10).
     * Used when hashing new passwords during registration and when verifying
     * credentials during login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider wires together the custom UserDetailsService
     * and the BCrypt encoder so that Spring Security can authenticate
     * username/password credentials.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} as a bean so that the
     * {@link com.assettrack.service.AuthService} can programmatically authenticate
     * login requests.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
