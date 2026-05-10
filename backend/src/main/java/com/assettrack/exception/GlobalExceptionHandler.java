package com.assettrack.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

/**
 * Centralised exception handler for the REST API layer.
 *
 * <p>
 * Translates domain exceptions and Spring validation failures into
 * consistent JSON error responses so that all error payloads follow the
 * same shape as described in the API specification sheet:
 * 
 * <pre>
 * {"error": "Human-readable message"}
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ──────────────────────────────────────────────────────────────────────────
    // Authentication & Authorization Exceptions
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Handles invalid login attempts. Maps to 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    /**
     * Handles unauthorized access to protected endpoints. Maps to 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, "Access denied: insufficient permissions");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Domain & Resource Exceptions
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Handles missing resources (e.g., Asset or User not found). Maps to 404 Not Found.
     */
    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleResourceNotFound(RuntimeException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles creation of duplicate resources (e.g., duplicate email or serial number).
     * Maps to 409 Conflict.
     */
    @ExceptionHandler({DuplicateResourceException.class, DataIntegrityViolationException.class, ConflictException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        // If it's a generic DataIntegrityViolationException, provide a friendly message.
        String message = ex instanceof DataIntegrityViolationException 
                ? "A resource with this unique identifier already exists." 
                : ex.getMessage();
        return error(HttpStatus.CONFLICT, message);
    }

    /**
     * Handles illegal state transitions. Maps to 409 Conflict or 400 Bad Request.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().startsWith("Cannot delete")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return error(status, ex.getMessage());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Validation Exceptions
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Handles DTO validation failures (@Valid, @NotBlank, etc.). Maps to 400 Bad Request.
     * Extracts all validation field errors and joins them into a single string message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        // Extract the specific field validation errors and format them into a single coherent message
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return error(HttpStatus.BAD_REQUEST, "Validation failed: " + errorMessage);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Catch-All & Web Exceptions
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Handles unsupported HTTP methods. Maps to 405 Method Not Allowed.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported: " + ex.getMessage());
    }

    /**
     * Catch-all for unexpected server errors. Maps to 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        // Use generic message to prevent leaking internals, but you could log 'ex' here
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /**
     * Helper method to construct the strict { "error": "..." } JSON structure.
     */
    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}