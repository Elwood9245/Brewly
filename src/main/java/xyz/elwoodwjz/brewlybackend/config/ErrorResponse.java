package xyz.elwoodwjz.brewlybackend.config;

import java.time.Instant;

/**
 * Standard API error payload across the application.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}
