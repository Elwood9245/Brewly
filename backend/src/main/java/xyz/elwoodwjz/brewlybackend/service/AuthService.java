package xyz.elwoodwjz.brewlybackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;


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
        username = username.trim();
        email = email.trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }
        User user = User.builder()
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