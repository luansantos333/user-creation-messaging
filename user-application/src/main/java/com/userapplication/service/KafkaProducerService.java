package com.userapplication.service;

import com.userapplication.dto.kafka.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "user-created";

    public KafkaProducerService(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.email(), event);
    }
}