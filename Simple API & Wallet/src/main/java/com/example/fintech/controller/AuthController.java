package com.example.fintech.controller;

import com.example.fintech.entity.User;
import com.example.fintech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Authentication Controller class
 */
@RestController
@RequestMapping("/api/fintech/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
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
}
