package com.virtucio.database;

/**
 * Custom exception for database-related errors.
 * 
 * Intent: Encapsulate SQL exceptions with application-specific context.
 * Allows UI layer to handle database errors separately from other exceptions.
 */
public class DatabaseException extends Exception {
    
    /**
     * Creates a new DatabaseException with a descriptive message.
     * 
     * @param message User-friendly error description
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Creates a new DatabaseException wrapping an underlying cause.
     * Preserves stack trace for debugging while providing context.
     * 
     * @param message User-friendly error description
     * @param cause The underlying exception (typically SQLException)
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
