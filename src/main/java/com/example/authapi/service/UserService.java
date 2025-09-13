package com.example.authapi.service;

import com.example.authapi.dto.RegisterRequest;
import com.example.authapi.entity.User;
import com.example.authapi.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostConstruct
    public void initializeMockUsers() {
        // Initialize mock users to match Next.js implementation
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User user1 = new User();
            user1.setUsername("user@example.com");
            user1.setEmail("user@example.com");
            user1.setPassword("password123"); // Store plain text password for mock data
            user1.setRole(User.Role.USER);
            userRepository.save(user1);
        }
        
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User user2 = new User();
            user2.setUsername("admin@example.com");
            user2.setEmail("admin@example.com");
            user2.setPassword("admin123"); // Store plain text password for mock data
            user2.setRole(User.Role.ADMIN);
            userRepository.save(user2);
        }
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
    
    public User findByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    public User createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        User user = new User();
        user.setUsername(request.email());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(User.Role.USER);
        
        return userRepository.save(user);
    }
    
    public boolean authenticateUser(String email, String password) {
        try {
            User user = findByEmail(email);
            // For mock users, compare plain text passwords
            return user.getPassword().equals(password);
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}
