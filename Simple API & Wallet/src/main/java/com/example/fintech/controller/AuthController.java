package com.example.fintech.controller;

import com.example.fintech.dto.ApiResponseWrapper;
import com.example.fintech.dto.ConfirmationCodeDTO;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

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
    public ResponseEntity<ApiResponseWrapper> register(@Valid @RequestBody User user) {
        logger.info("AuthController | METHOD: register() - REGISTER OPERATION");

        userService.registerUser(user);

        return ResponseEntity.ok(
                ApiResponseWrapper.success("User registered successfully")
        );
    }

    /**
     * Login User endpoint
     * @param loginRequest
     * @return String
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("AuthController | METHOD: login() - LOGIN OPERATION");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", jwt));
    }


    /**
     * Check user confirmationCode Endpoint
     * introduce in 'userName' your account username and you will be able to check your confirmation code
     * @param userName
     * @return ResponseEntity<ConfirmationCodeDTO>
     */
    @GetMapping("/check-code/{userName}")
    public ResponseEntity<ConfirmationCodeDTO> checkCode(@PathVariable("userName") String userName) {
        logger.info("AuthController | METHOD: checkCode() - CHECK USER CONFIRMATION CODE OPERATION");
        return ResponseEntity.ok(userService.checkUserCode(userName));
    }


    /**
     * Confirmation code User Endpoint
     * @param userName
     * @param code
     * @return String
     */
    @PostMapping("/confirm-code/{userName}")
    public ResponseEntity<String> confirmCode(@PathVariable("userName") String userName, @RequestParam String code) {
        logger.info("AuthController | METHOD: confirmCode() - USER CONFIRMATION CODE INPUT OPERATION");
        userService.confirmUser(userName, code);
        return ResponseEntity.ok("Account confirmed");
    }

}
