package com.assettrack.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utility component for creating, parsing, and validating JSON Web Tokens.
 *
 * <p>Uses HMAC-SHA256 (HS256) signing with a secret loaded from
 * {@code app.jwt.secret} in {@code application.properties}.
 *
 * <p>Token payload:
 * <ul>
 *     <li>{@code sub} — the user's email address (Spring Security username)</li>
 *     <li>{@code iat} — issued-at timestamp</li>
 *     <li>{@code exp} — expiry timestamp (issued-at + configured expiry-ms)</li>
 * </ul>
 */
@Component
public class JwtTokenProvider {

    /** HS256 signing key derived from the configured secret. */
    private final SecretKey signingKey;

    /** Token validity in milliseconds (default: 24 hours). */
    private final long expiryMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiry-ms:86400000}") long expiryMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiryMs = expiryMs;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Token Generation
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Generates a signed JWT for the given authenticated user.
     *
     * @param userDetails the authenticated principal
     * @return a compact, signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Token Parsing
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Extracts the subject (email) from a token without performing validation.
     * Should only be called after {@link #validateToken(String)} returns {@code true}.
     *
     * @param token the compact JWT string
     * @return the subject claim (email address)
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Token Validation
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Validates the token's signature and checks it has not expired.
     *
     * @param token the compact JWT string to validate
     * @return {@code true} if the token is valid and unexpired; {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
