package com.example.fintech.controller;

import com.example.fintech.dto.LoginRequestDTO;
import com.example.fintech.entity.User;
import com.example.fintech.security.CustomUserDetailsService;
import com.example.fintech.service.JwtService;
import com.example.fintech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Authentication Controller class
 */
@RestController
@RequestMapping("/api/fintech/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final static Logger logger = LogManager.getLogger(AuthController.class);

    /**
     * Register User endpoint
     * @param user
     * @return User
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        logger.info("AuthController | METHOD: register() - REGISTER OPERATION");
        return ResponseEntity.ok(userService.registerUser(user));
    }

    /**
     * Login User endpoint
     * @param loginRequest
     * @return String
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("AuthController | METHOD: login() - LOGIN OPERATION");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        //final UserDetails userDetails = (UserDetails) userService.getUserByEmail(loginRequest.getEmail());
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(jwt);
    }



    // MELHORAR PARTE DA CONFIRMACAO
    // deve ser feito endpoint para cada email confirmar o codigo
    // deve ser feito endpoint com base no email para confirmar o codigo
    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String email, @RequestParam String code) {
        userService.confirmUser(email, code);
        return ResponseEntity.ok("Account confirmed");
    }

}
