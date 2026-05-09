package com.assettrack.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

/**
 * Centralised exception handler for the REST API layer.
 *
 * <p>Translates domain exceptions and Spring validation failures into
 * consistent JSON error responses so that all error payloads follow the
 * same shape as described in the API specification sheet:
 * <pre>{"error": "Human-readable message"}</pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ──────────────────────────────────────────────────────────────────────────
    // Domain Exceptions
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 404 — returned when a requested entity (asset, user, etc.) does not exist.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 400 / 409 — returned for business rule violations such as:
     * <ul>
     *     <li>Allocating an already-assigned asset</li>
     *     <li>Deleting an assigned asset</li>
     *     <li>Duplicate serial number on registration</li>
     * </ul>
     * The HTTP status is chosen by the caller (CONFLICT vs BAD_REQUEST).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        // If the message contains "Cannot delete" we return 409 Conflict;
        // all other business-rule violations return 400 Bad Request.
        HttpStatus status = ex.getMessage() != null && ex.getMessage().startsWith("Cannot delete")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return error(status, ex.getMessage());
    }

    /**
     * 403 — Spring Security throws this when a user lacks the required role.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, "Access denied: insufficient permissions");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Validation Exceptions
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 400 — triggered when a {@code @Valid} annotated request body fails Bean Validation.
     * Returns a map of field-name → constraint-message pairs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        body.put("details", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Catch-All
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 500 — fallback for any unexpected exception not handled above.
     * The detail message is deliberately generic to avoid leaking internals.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
