package com.example.fintech.security;

import com.example.fintech.entity.User;
import com.example.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService Service Class
 *
 * Responsible to retrieve user data from database during authentication process
 * Implements Spring Security's UserDetailsService interface, used by AuthenticationManager to retrieve user information
 * When a user try to log in, Spring Security calls this service to check user by username (email)
 * If user is found returns CustomUserDetails object, that Spring Security can use for authentication and authorization
 *
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Check if user exists by email in Database for authentication porpoise
     * Automatically called by Spring Security during authentication
     * @param email
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user);
    }

}
