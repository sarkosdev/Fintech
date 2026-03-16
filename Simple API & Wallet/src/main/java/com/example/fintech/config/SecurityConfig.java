package com.example.fintech.config;

import com.example.fintech.security.CustomUserDetailsService;
import com.example.fintech.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig
 *
 * This class centralizes all Spring Security configuration.
 *
 * Responsibilities:
 * 1. Defines which endpoints are public and which require authentication
 * 2. Configures stateless session management (JWT-based authentication)
 * requests to our server come with a JWT token, no need for our application
 * know who we are or what we did
 * 3. Registers authentication provider (DB + BCrypt) - Verify User Credentials.
 * 4. Adds custom JWT filter to validate tokens on each request.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Constructor Dependency Injection Section - @RequiredArgsConstructor
     */
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Main security configuration
     *
     * - Disables CSRF (because we use stateless JWT)
     * - Defines public endpoints
     * - Defines private/authenticated endpoints
     * - Configures stateless session management
     * - Adds JWT filter (intercepts HTTP requests, validates JWT token)
     * before UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .csrf().disable()
                .authenticationProvider(authenticationProvider())
                .authorizeRequests()
                .antMatchers("/api/fintech/auth/**").permitAll()
                .antMatchers("/api/fintech/transaction/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Custom JWT filter that:
     * - Extracts token from Authorization header
     * - Validates token
     * - Sets authentication in SecurityContext
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, customUserDetailsService);
    }

    /**
     * Password encoder used for hashing user passwords (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider that:
     * - Uses CustomUserDetailsService to load user from DB
     * - Uses BCryptPasswordEncoder to verify passwords
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposes AuthenticationManager bean
     * Used in AuthController to authenticate login requests
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
