package com.userapplication.service;

import com.userapplication.dto.kafka.UserAdminAccessGrant;
import com.userapplication.dto.kafka.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final KafkaTemplate<String, UserAdminAccessGrant> kafkaAdminTemplate;

    public KafkaProducerService(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate, KafkaTemplate<String, UserAdminAccessGrant> kafkaAdminTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAdminTemplate = kafkaAdminTemplate;
    }

    public void sendUserCreatedEvent(String topic, UserCreatedEvent event) {
        kafkaTemplate.send(topic, event.email(), event);
    }

    public void sendUserHasBeenGrantedAdminAccess (String topic, UserAdminAccessGrant  userAdminAccessGrant) {

        kafkaAdminTemplate.send(topic, userAdminAccessGrant.email(), userAdminAccessGrant);

    }

}