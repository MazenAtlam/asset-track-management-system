package com.assettrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts incoming HTTP requests to validate JWTs.
 * Ensures the user is authenticated statelessly before accessing protected
 * endpoints.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Check if the request contains an Authorization header with a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract token from the header
        jwt = authHeader.substring(7);

        try {
            // 3. Extract the username (email) from the token
            userEmail = jwtUtil.extractUsername(jwt);

            // 4. If the email is valid and the user isn't currently authenticated in the
            // security context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load the user details from our database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 5. Validate the token against the loaded user details
                if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {

                    // 6. Create an authentication token and update the Security Context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ex) {
            // If token is invalid, expired, or malformed, clear context and let the entry
            // point handle it
            SecurityContextHolder.clearContext();
        }

        // 7. Proceed to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}