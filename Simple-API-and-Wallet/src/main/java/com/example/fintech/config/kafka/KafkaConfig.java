package com.example.fintech.config.kafka;

import com.example.fintech.dto.RequestEvent;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration class
 *
 * Defines Producer Configuration
 * Serialization strategy for keys and values
 * Reliability settings (retries, acknowledgments)
 * Kafka template bean used to send messages to Kafka Topics
 *
 */
@Configuration
public class KafkaConfig {

    /**
     * Kafka ProducerFactory Bean configuration
     *
     * ProducerFactory is responsible to create kafka producers that will eventually publish messages in to
     * Kafka topic
     * @return
     */
    @Bean
    public ProducerFactory<String, RequestEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * KafkaTemplate Bean Configuration
     * Simplifies the process of publishing messages in to our Topic
     * @return
     */
    @Bean
    public KafkaTemplate<String, RequestEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
