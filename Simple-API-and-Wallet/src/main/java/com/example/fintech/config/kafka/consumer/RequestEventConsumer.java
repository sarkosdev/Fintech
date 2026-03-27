package com.example.fintech.config.kafka.consumer;

import com.example.fintech.dto.RequestEvent;
import com.example.fintech.entity.Account;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Kafka Consumer Service class
 */
@Service
@RequiredArgsConstructor
public class RequestEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(RequestEventConsumer.class);
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Listen to RequestEvents that are sent via Kafka
     * @param event
     * @param key
     */
    @KafkaListener(
            topics = "requests-fintech",
            groupId = "fintech-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void receiveRequestEvent(RequestEvent event, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("Intercepted request via Kafka with ID [key={}]: {}", key, event.toString());

        handlesAccountCreationRequest(event.getUserEmail(), event.getGiveAwayFreeAmount());
    }

    /**
     * Handles account creation for the specific User and assign amount given away
     * @param userName
     * @param giveAwayFreeAmount
     */
    private void handlesAccountCreationRequest(String userName, BigDecimal giveAwayFreeAmount) {
        User user = userRepository.findByEmail(userName)
                .orElseThrow(
                        () -> new BusinessException("User not found in Database for the following email: " + userName)
                );

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setBalance(giveAwayFreeAmount);

        accountRepository.save(newAccount);
    }

}
