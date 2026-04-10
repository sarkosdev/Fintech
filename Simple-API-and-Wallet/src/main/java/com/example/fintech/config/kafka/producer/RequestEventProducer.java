package com.example.fintech.config.kafka.producer;

import com.example.fintech.dto.RequestEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka Producer Class, used to send specific messages (RequestEvent) to our Kafka Topic
 */
@Service
@RequiredArgsConstructor
public class RequestEventProducer {

    private static final Logger log = LoggerFactory.getLogger(RequestEventProducer.class);
    private final KafkaTemplate<String, RequestEvent> kafkaTemplate;                            // Sender Template
    private static final String TOPIC = KafkaProducerConfig.TOPIC;                              // Topic Configuration

    /**
     * Method responsible to send message (RequestEvent) to Kafka Topic
     * @param payload
     */
    public void send(RequestEvent payload) {

        // Order requests by ID
        String key = payload.getId().toString();

        kafkaTemplate.send(
                        TOPIC,
                        key,
                        payload)
                .addCallback(
                        result -> log.info("Request sent successfully [key={}]", key),
                        ex -> log.error("Error sending message [key={}]", key)
                );

        log.info("Message sent to Topic {} with value: {}", TOPIC, payload.toString());
    }


}
