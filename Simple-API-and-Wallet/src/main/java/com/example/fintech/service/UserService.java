package com.example.fintech.service;

import com.example.fintech.config.kafka.producer.RequestEventProducer;
import com.example.fintech.dto.ConfirmationCodeDTO;
import com.example.fintech.dto.RequestEvent;
import com.example.fintech.dto.UserPayload;
import com.example.fintech.entity.Outbox;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.OutboxRepository;
import com.example.fintech.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

/**
 * User Service class
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final PasswordEncoder passwordEncoder;                           //Encodes password
    private final RequestEventProducer requestEventProducer;


    /**
     * Register User
     * @param user
     * @return
     */
    @Transactional
    public User registerUser(User user) {
        logger.info("UserService | METHOD: registerUser() - REGISTER A NEW USER: {}", user.getEmail());
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("Email already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmedAccount(false);
        user.setConfirmationCode(generateConfirmationCode(user.getEmail()));

        logger.info("UserService | METHOD: registerUser() ABOUT TO SAVE USER: {}", user.toString());
        return userRepository.save(user);
    }

    /**
     * Checks if user exists on DB, checks if code provided matches the code
     * associated to our User
     * @param email
     * @param code
     * @return User
     */
    @Transactional
    public void confirmUser(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if(user.isConfirmedAccount()) throw new BusinessException("This account is already confirmed");

        if (!Objects.equals(user.getConfirmationCode(), code)) throw new BusinessException("Invalid confirmation code");

        user.setConfirmedAccount(true);

        try {
            userRepository.save(user);

            String payloadJson = userPayloadData(user);

            if(!payloadJson.isEmpty()) {
                Outbox event = new Outbox();
                event.setEventType("WALLET_CREATION");
                event.setPayload(payloadJson);
                event.setStatus("PENDING");

                outboxRepository.save(event);
            }

            // If it saved successfully send RequestEvent for our Kafka Topic (Message Driven Design)
            // In order to create an Account for this User
            //sendToKafkaProducer(user);
        }
        catch (Exception e) {
            logger.error("Error during confirmUser OPERATION", e);
            throw new RuntimeException("Failed Operation");
        }
    }

    /**
     * Finds user confirmationCode in DB by Email in order to check
     * confirmationCode for User
     * @param email
     * @return ConfirmationCodeDTO
     */
    public ConfirmationCodeDTO checkUserCode(String email){
        return userRepository.findConfirmationCodeByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
    }




    //-----------PRIVATE OPERATIONS SECTIONS-----------------------//

    /**
     * Generates 6 digits code in order to be used for email confirmation
     * @return String
     */
    private String generateConfirmationCode(String email){
        logger.info("UserService | METHOD: generateConfirmationCode() GENERATE CONFIRMATION CODE for email: {}"
                , email);

        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        logger.info("UserService | METHOD: generateConfirmationCode() CODE GENERATED: {}", code);
        return String.valueOf(code);
    }

    /**
     * CURRENTLY DEPRECATED - SWAPPED WITH OUTBOX DESIGN PATTERN APPROACH
     *
     * Calls our Kafka topic in order to send RequestEvent from Producer to Consumer
     * The goal is to create User Wallet after User is confirmed in order to use the Wallet
     */
    private void sendToKafkaProducer(User user){

        RequestEvent requestEvent = new RequestEvent();
        requestEvent.setId(user.getId());
        requestEvent.setEmail(user.getEmail());
        requestEvent.setGiveAwayFreeAmount(new BigDecimal(100));

        logger.info("UserService | METHOD: sendToKafkaProducer() ABOUT TO SEND MESSAGE with payload: {}"
                , requestEvent.toString());

        requestEventProducer.send(requestEvent);
    }

    /**
     * Converts User 'id' and 'email' in to a String payload for Outbox Design Pattern
     * @param user
     * @return
     */
    private String userPayloadData(User user) {
        UserPayload payload = new UserPayload();
        String payloadReturn = new String();

        payload.setId(user.getId());
        payload.setEmail(user.getEmail());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            payloadReturn = objectMapper.writeValueAsString(payload);
        }
        catch (JsonProcessingException e) {
            logger.error("Error processing payload, {}", e.getMessage());
        }

        return payloadReturn;
    }

}
