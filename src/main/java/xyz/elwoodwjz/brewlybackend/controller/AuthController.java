package xyz.elwoodwjz.brewlybackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.elwoodwjz.brewlybackend.dto.auth.RegisterRequest;
import xyz.elwoodwjz.brewlybackend.dto.auth.LoginRequest;
import xyz.elwoodwjz.brewlybackend.dto.auth.AuthResponse;
import xyz.elwoodwjz.brewlybackend.dto.user.UserResponse;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.service.AuthService;
import xyz.elwoodwjz.brewlybackend.security.JwtUtil;


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

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getUsername(), request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(user);
        UserResponse userResponse = new UserResponse(user.getId().toString(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request.getAccount(), request.getPassword());
        String token = jwtUtil.generateToken(user);
        UserResponse userResponse = new UserResponse(user.getId().toString(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, userResponse));
    }
}
