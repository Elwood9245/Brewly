package xyz.elwoodwjz.brewlybackend.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This should result in a 404 Not Found response.
 *
 * @author Junzhe Wu
 * @since 2025-08-18
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
