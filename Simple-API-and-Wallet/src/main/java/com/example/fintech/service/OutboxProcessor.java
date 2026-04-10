package com.example.fintech.service;

import com.example.fintech.config.kafka.producer.RequestEventProducer;
import com.example.fintech.dto.RequestEvent;
import com.example.fintech.entity.Outbox;
import com.example.fintech.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * OutboxProcessor Workload Class - Outbox Design Pattern
 */
@Component
@AllArgsConstructor
public class OutboxProcessor {

    private static final Logger logger = LogManager.getLogger(OutboxProcessor.class);
    private final RequestEventProducer requestEventProducer;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;


    /**
     * Outbox Workload method
     *
     * Checks DB in order to check if there is any event on 'PENDING' state, if yes,
     * processes it and sends it to Kafka
     */
    @Scheduled(fixedDelay = 10000)
    public void processOutbox() {

        logger.info("Outbox Worker has been executed -> START");

        List<Outbox> events = outboxRepository.findTop20ByStatusOrderByTimestampAsc("PENDING");

        for (Outbox event : events) {
            try {
                sendToKafkaProducer(event);

                event.setStatus("SENT");
            } catch (Exception e) {
                logger.info("Couldn't process it properly, retying later, {}", event.toString());
                logger.error("Exception Thrown: {}", e.getMessage());
            }
        }

        outboxRepository.saveAll(events);

        logger.info("Outbox Worker has been paused -> END");
    }


    /**
     * Calls our Kafka topic in order to send RequestEvent from Producer to Consumer
     * The goal is to create User Wallet after User is confirmed in order to use the Wallet
     */
    private void sendToKafkaProducer(Outbox payload) {
        try {
            RequestEvent requestEvent = objectMapper.readValue(payload.getPayload(), RequestEvent.class);
            requestEvent.setGiveAwayFreeAmount(new BigDecimal(100));
            requestEventProducer.send(requestEvent);
            logger.info("OutboxProcesso | METHOD: sendToKafkaProducer() ABOUT TO SEND MESSAGE with payload: {}"
                    , requestEvent.toString());
        } catch (JsonProcessingException e) {
            logger.error("OutboxProcessor JsonProcessing failed with following exception: {}", e.getMessage());
        }
    }

}
