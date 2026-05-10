package com.assettrack.exception;

/**
 * Exception thrown when a resource cannot be created or updated because
 * it would violate a uniqueness constraint (e.g., duplicate email or serial number).
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
}
