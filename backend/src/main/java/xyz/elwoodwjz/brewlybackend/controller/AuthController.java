package xyz.elwoodwjz.brewlybackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.elwoodwjz.brewlybackend.dto.auth.RegisterRequest;
import xyz.elwoodwjz.brewlybackend.dto.auth.LoginRequest;
import xyz.elwoodwjz.brewlybackend.dto.auth.AuthResponse;
import xyz.elwoodwjz.brewlybackend.dto.user.UserResponse;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.service.AuthService;
import xyz.elwoodwjz.brewlybackend.security.JwtUtil;
import xyz.elwoodwjz.brewlybackend.security.CustomUserDetailsService.CustomUserPrincipal;

import java.util.UUID;

/**
 * Controller for handling authentication-related endpoints.
 *
 * @author Junzhe Wu
 * @since 2025-07-13
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // Helper method to get user from authentication
    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
        }
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getUsername(), request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(user);
        UserResponse userResponse = mapToUserResponse(user);
        return ResponseEntity.ok(new AuthResponse(token, userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request.getAccount(), request.getPassword());
        String token = jwtUtil.generateToken(user);
        UserResponse userResponse = mapToUserResponse(user);
        return ResponseEntity.ok(new AuthResponse(token, userResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        UserResponse userResponse = mapToUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Maps User entity to UserResponse DTO
     * @param user the User entity
     * @return UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId().toString(),
            user.getUsername(),
            user.getEmail()
        );
    }
}
