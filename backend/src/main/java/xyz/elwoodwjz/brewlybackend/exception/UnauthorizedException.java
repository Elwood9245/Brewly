package xyz.elwoodwjz.brewlybackend.exception;

/**
 * Exception thrown when a user tries to access a resource they don't own.
 * This should result in a 403 Forbidden response.
 *
 * @author Junzhe Wu
 * @since 2025-08-18
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
