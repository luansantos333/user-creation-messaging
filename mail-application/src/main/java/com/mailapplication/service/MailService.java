package com.mailapplication.service;

import com.mailapplication.dto.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "user-created", groupId = "user-created-group")
    public UserCreatedEvent sendEmail(UserCreatedEvent userCreatedEvent) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        logger.info("Sending user-created email...");

        mailMessage.setSubject("Your new user has been successfully created");
        mailMessage.setText("Hello, \n\n" + "We are happy to announce that your account has been successfully created. ");
        mailMessage.setTo(userCreatedEvent.email());
        mailSender.send(mailMessage);

        logger.info("Email sent successfully...");

        return userCreatedEvent;

    }


}
