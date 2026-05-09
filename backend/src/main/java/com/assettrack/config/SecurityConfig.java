package com.assettrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.assettrack.security.JwtAuthenticationFilter;

/**
 * Spring Security filter-chain configuration.
 *
 * <p>Key decisions:
 * <ul>
 *     <li>CSRF is disabled — the API is stateless and authenticates exclusively via JWT.</li>
 *     <li>Session creation is set to STATELESS — no {@code HttpSession} is ever created.</li>
 *     <li>Auth endpoints ({@code /api/v1/auth/**}) are public; everything else requires a valid JWT.</li>
 *     <li>Fine-grained role checks (ADMIN, MANAGER) are enforced with {@code @PreAuthorize}
 *         annotations in the service / controller layer, enabled by {@code @EnableMethodSecurity}.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize / @Secured on controllers and services
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── Disable CSRF (stateless JWT API) ──────────────────────────────
            .csrf(AbstractHttpConfigurer::disable)

            // ── Stateless session — never create an HttpSession ───────────────
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── Authorisation rules ───────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Public: authentication endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Everything else requires a valid JWT
                .anyRequest().authenticated()
            )

            // ── JWT filter runs before the standard username/password filter ──
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
