package com.userapplication.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {


    @Bean
    public NewTopic userCreatedTopic() {

        return TopicBuilder.name("user-created")
                .partitions(2)
                .replicas(1)
                .build();

    }


    @Bean
    public NewTopic adminGrantEvent () {


        return TopicBuilder.name("admin-grant")
                .partitions(2)
                .replicas(1)
                .build();


    }

    @Bean
    public NewTopic passwordResetEvent () {

        return TopicBuilder.name("password-reset")
                .partitions(2)
                .replicas(1)
                .build();

    }


}
