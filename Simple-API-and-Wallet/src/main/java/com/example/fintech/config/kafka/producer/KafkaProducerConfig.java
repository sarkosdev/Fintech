package com.example.fintech.config.kafka.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Producer Configuration Class
 */
@Configuration
public class KafkaProducerConfig {

    // TOPIC'S NAME
    public static final String TOPIC = "requests-fintech";

    /**
     * Kafka Topic Configuration stream of messages where messages are sent to and retrieved from
     * - Producer send messages to this topic
     * - Messages are stored in this topic
     * - Consumer reads the messages from this topic
     * @return
     */
    @Bean
    public NewTopic topic(){
        return TopicBuilder.name(TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }



}
