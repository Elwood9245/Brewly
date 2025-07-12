package xyz.elwoodwjz.brewlybackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


/**
 * Service for user authentication and registration
 * Apply basic validation for username, email, and password
 * 
 * @author Junzhe Wu
 * @since 2025-07-12
 */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Register a new user
     * 
     * @param username the username to register
     * @param email the email to register
     * @param password the password to register
     * @return the registered user
     */
    public User register(String username, String email, String password) {
        if (username == null || username.trim().isEmpty() || username.length() > 50) {
            throw new IllegalArgumentException("Username must be 1-50 characters");
        }
        if (email == null || email.trim().isEmpty() || email.length() > 100) {
            throw new IllegalArgumentException("Email must be 1-100 characters");
        }
        if (password == null || password.trim().isEmpty() || password.length() < 6 || password.length() > 64) {
            throw new IllegalArgumentException("Password must be 6-64 characters");
        }
        String emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
        username = username.trim();
        email = email.trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    /**
     * Login a user by account and password
     * 
     * @param account the account to login (email or username)
     * @param password the password to login
     * @return the logged in user
     */
    public User login(String account, String password) {
        if (account == null || account.trim().isEmpty() || account.length() > 100) {
            throw new IllegalArgumentException("Account must not be empty or too long");
        }
        if (password == null || password.trim().isEmpty() || password.length() > 64) {
            throw new IllegalArgumentException("Password must not be empty or too long");
        }
        account = account.trim();
        Optional<User> userOpt = userRepository.findByEmail(account.toLowerCase());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(account);
        }
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid account or password");
        }
        return userOpt.get();
    }

    /**
     * Delete a user by account
     * 
     * @param account the account to delete (email or username)
     * @return the deleted user
     */
    public User delete(String account) {
        if (account == null || account.trim().isEmpty() || account.length() > 100) {
            throw new IllegalArgumentException("Account must not be empty or too long");
        }
        account = account.trim();
        Optional<User> userOpt = userRepository.findByEmail(account.toLowerCase());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(account);
        }
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        userRepository.delete(userOpt.get());
        return userOpt.get();
    }
} 