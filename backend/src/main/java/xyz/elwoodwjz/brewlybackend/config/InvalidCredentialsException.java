package xyz.elwoodwjz.brewlybackend.config;

/**
 * Exception thrown when login credentials are invalid.
 * This should result in a 401 Unauthorised response.
 *
 * @author Junzhe Wu
 * @since 2025-08-12
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
