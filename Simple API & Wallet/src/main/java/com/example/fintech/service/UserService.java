package com.example.fintech.service;

import com.example.fintech.controller.AuthController;
import com.example.fintech.entity.User;
import com.example.fintech.exception.ResourceNotFoundException;
import com.example.fintech.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * User Service class
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final static Logger logger = LogManager.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;                                          //Encodes password


    /**
     * Register User
     * @param user
     * @return
     */
    public User registerUser(User user) {
        logger.info("UserService | METHOD: registerUser() - REGISTER A NEW USER: {}", user.toString());
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmedAccount(true);
        user.setConfirmationCode(generateConfirmationCode(user.getEmail()));

        // Send an email with the confirmation code in order to confirm email
        logger.info("UserService | METHOD: registerUser() ABOUT TO SEND EMAIL WITH CONFIRMATION CODE");
        //emailService.sendEmailConfirmationMethod(user);

        logger.info("UserService | METHOD: registerUser() ABOUT TO SAVE USER: {}", user.toString());
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );
    }



    //-----------PRIVATE OPERATIONS SECTIONS-----------------------//
    /**
     * Generates 6 digits code in order to be used for email confirmation
     * @return String
     */
    private String generateConfirmationCode(String email){
        logger.info("UserService | METHOD: generateConfirmationCode() GENERATE CONFIRMATION CODE for email: {}", email);
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        logger.info("UserService | METHOD: generateConfirmationCode() CODE GENERATED: {}", code);
        return String.valueOf(code);
    }



    // MELHORAR PARTE DA CONFIRMACAO
    public void confirmUser(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!user.getConfirmationCode().equals(code)) {
            throw new IllegalStateException("Invalid confirmation code");
        }

        user.setConfirmedAccount(true);
        userRepository.save(user);
    }

}
