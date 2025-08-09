package xyz.elwoodwjz.brewlybackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.elwoodwjz.brewlybackend.entity.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for handling JWT operations.
 *
 * @author Junzhe Wu
 * @since 2025-07-13
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInSeconds;

    /**
     * Getter for the signing key.
     * @return SecretKey for JWT operations.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generates a JWT for the specified user.
     * @param user The user for whom the token is generated.
     * @return A signed JWT.
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationInSeconds, ChronoUnit.SECONDS);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .issuedAt(new Date(now.toEpochMilli()))
                .expiration(new Date(expiration.toEpochMilli()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the provided JWT.
     * @param token The JWT to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Determines whether the token has expired.
     * @param token The JWT to check.
     * @return True if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extracts the user ID from the JWT.
     * @param token The JWT from which to extract the user ID.
     * @return The user's UUID.
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extracts the username from the JWT.
     * @param token The JWT from which to extract the username.
     * @return The username as a String.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    /**
     * Extracts the email address from the JWT.
     * @param token The JWT from which to extract the email.
     * @return The email address as a String.
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    /**
     * Parses the JWT and retrieves its claims.
     * @param token The JWT to parse.
     * @return Claims contained within the token.
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
} 