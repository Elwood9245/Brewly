package xyz.elwoodwjz.brewlybackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Implement UserDetailsService interface for Spring Security.
 * Supports loading users by username or by unique identifier (UUID).
 *
 * @author Junzhe Wu
 * @since 2025-07-13
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username.
     * @param username The username to search for.
     * @return UserDetails for authentication.
     * @throws UsernameNotFoundException if the user does not exist.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserPrincipal(user);
    }

    /**
     * Loads a user by their unique identifier (UUID).
     * @param userId The UUID of the user.
     * @return UserDetails for authentication.
     * @throws UsernameNotFoundException if the user does not exist.
     */
    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return new CustomUserPrincipal(user);
    }

    /**
     * Custom UserDetails implementation.
     * Provides authorities and account status for Spring Security.
     */
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // All users are ROLE_USER. Can be further extended to support more roles.
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }
        
        // No following attributes in User entity so it always returns true.
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
} 